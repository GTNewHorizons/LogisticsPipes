package logisticspipes.blocks.powertile;

import com.github.bsideup.jabel.Desugar;
import logisticspipes.Tags;
import logisticspipes.blocks.LogisticsSolidTileEntity;
import logisticspipes.gui.hud.HUDPowerLevel;
import logisticspipes.interfaces.*;
import logisticspipes.interfaces.routing.IFilter;
import logisticspipes.network.NewGuiHandler;
import logisticspipes.network.PacketHandler;
import logisticspipes.network.abstractguis.CoordinatesGuiProvider;
import logisticspipes.network.guis.block.PowerProviderGui;
import logisticspipes.network.packets.block.PowerProviderLevel;
import logisticspipes.network.packets.hud.HUDStartBlockWatchingPacket;
import logisticspipes.network.packets.hud.HUDStopBlockWatchingPacket;
import logisticspipes.pipes.basic.CoreRoutedPipe;
import logisticspipes.pipes.basic.LogisticsTileGenericPipe;
import logisticspipes.proxy.MainProxy;
import logisticspipes.proxy.SimpleServiceLocator;
import logisticspipes.proxy.computers.interfaces.CCCommand;
import logisticspipes.proxy.computers.interfaces.CCType;
import logisticspipes.renderer.LogisticsHUDRenderer;
import logisticspipes.routing.ExitRoute;
import logisticspipes.routing.IRouter;
import logisticspipes.routing.PipeRoutingConnectionType;
import logisticspipes.utils.PlayerCollectionList;
import logisticspipes.utils.WorldUtil;
import logisticspipes.utils.item.ItemIdentifierInventory;
import logisticspipes.utils.tuples.Pair;
import logisticspipes.utils.tuples.Triplet;
import lombok.Getter;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@CCType(name = "LogisticsPowerProvider")
public abstract class LogisticsPowerProviderTileEntity extends LogisticsSolidTileEntity
    implements IGuiTileEntity, ISubSystemPowerProvider, IPowerLevelDisplay, IGuiOpenControler,
    IHeadUpDisplayBlockRendererProvider, IBlockWatchingHandler, ISidedInventory {

    public static final int RF_COLOR = 0xff0000;
    public static final int IC2_COLOR = 0xffff00;
    public static final short BATTERY_COUNT = 9;
    private static final short HISTORY_COUNT = 32;

    // true if it needs more power, turns off at full, turns on at 50%.
    public boolean needMorePowerTriggerCheck = true;

    protected List<PowerOrder> orders = new ArrayList<>();
    protected List<Pair<CoreRoutedPipe, ForgeDirection>> adjacentPipes = new ArrayList<>();
    protected final ItemIdentifierInventory inventory = new ItemIdentifierInventory(BATTERY_COUNT, "PowerProvider Inventory", 1);
    private final IHeadUpDisplayRenderer HUD = new HUDPowerLevel(this);
    private final PlayerCollectionList guiListener = new PlayerCollectionList();
    private final PlayerCollectionList watcherList = new PlayerCollectionList();
    public final double[] energyHistory = new double[HISTORY_COUNT];

    protected double currentEnergy = 0;
    protected double maxEnergy = 0;
    /**
     * -- GETTER --
     *
     * @return the average energy IO over the last {@code HISTORY_COUNT} ticks
     */
    @Getter
    private double averageIO = 0;
    private short tickTimer = 0;
    private boolean initialized;
    WorldUtil worldUtil;


    protected LogisticsPowerProviderTileEntity() {
        System.out.println("new LogisticsPowerProviderTileEntity at " + xCoord + ", " + yCoord + ", " + zCoord);
        inventory.addListener(e -> updateCapacity());
    }

    /**
     * Updates the capacity of the internal buffer.
     * Gets called on inventory change
     */
    protected abstract void updateCapacity();

    /**
     * @return The maximum voltage for the IO of this PowerProvider
     */
    protected abstract double getMaxEnergyIO();

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (!initialized) initialize();

        if (MainProxy.isClient(getWorld())) doClientEntityUpdate();
        else doServerEntityUpdate();
    }

    private void doServerEntityUpdate() {
        updateAdjacentPipes();
        if (!adjacentPipes.isEmpty())
            handleOrders();
        orders.clear();

        if (++tickTimer >= HISTORY_COUNT) tickTimer = 0;
        energyHistory[tickTimer] = currentEnergy;
        double average = 0;

        for (int i = 0; i < HISTORY_COUNT - 1; i++) {
            var index1 = (tickTimer + i + 1) % HISTORY_COUNT;
            var index2 = (tickTimer + i + 2) % HISTORY_COUNT;
            average += (energyHistory[index2] - energyHistory[index1]);
        }

        averageIO = average / (HISTORY_COUNT - 1);

        updateClientSend();
    }

    private void doClientEntityUpdate() {
        //
    }

    private void initialize() {
        if (MainProxy.isClient(getWorld())) {
            LogisticsHUDRenderer.instance().add(this);
        }

        worldUtil = new WorldUtil(getWorldObj(), getX(), getY(), getZ());
        initialized = true;
    }

    /**
     * Updates the adjacent pipes that are connected to this tile entity transmit power
     */
    private void updateAdjacentPipes() {
        adjacentPipes.clear();

        for (var adjacent : worldUtil.getAdjacentTileEntities()) {
            if (!(adjacent.tile instanceof LogisticsTileGenericPipe logisticsTileGenericPipe)) continue;
            if (!(logisticsTileGenericPipe.pipe instanceof CoreRoutedPipe coreRoutedPipe)) continue;
            if (coreRoutedPipe.stillNeedReplace() || !coreRoutedPipe.isInitialized()) continue;

            IRouter sourceRouter = coreRoutedPipe.getRouter();
            if (sourceRouter == null) continue;

            adjacentPipes.add(new Pair<>(coreRoutedPipe, adjacent.orientation));
        }
    }

    /**
     * Handles all present ordes that were send by any pipes in the same network, that want power.
     * Provides power to these orders one by one, until the internal buffer is empty
     */
    private void handleOrders() {
        double sendEnergyThisTick = 0;

        orders:
        for (PowerOrder order : orders) {
            if (currentEnergy == 0) return;
            if (sendEnergyThisTick >= getMaxEnergyIO()) return;

            double toSend = Math.min(Math.min(order.requestAmount(), currentEnergy), sendEnergyThisTick);
            if (toSend == 0) continue;

            IRouter destinationRouter = SimpleServiceLocator.routerManager.getRouter(order.destinationID());
            if (destinationRouter == null || destinationRouter.getPipe() == null) continue;

            //collect routes from source to end router
            for (Pair<CoreRoutedPipe, ForgeDirection> adjacent : adjacentPipes) {
                CoreRoutedPipe pipe = adjacent.getValue1();
                IRouter sourceRouter = pipe.getRouter();
                if (sourceRouter == null) continue;

                for (var route : sourceRouter.getDistanceTo(destinationRouter)) {
                    if (!route.containsFlag(PipeRoutingConnectionType.canPowerSubSystemFrom)) continue;
                    if (route.filters.stream().anyMatch(IFilter::blockPower)) continue;

                    pipe.container.addLaser(adjacent.getValue2().getOpposite(), 1, getLaserColor(), true, true);
                    sendPowerLaserPackets(sourceRouter, destinationRouter, route.exitOrientation, route.exitOrientation != adjacent.getValue2());
                    currentEnergy -= toSend;
                    sendPowerToPipe(route, toSend);

                    continue orders;
                }
            }
        }
    }

    /**
     * Sends power to the destination of the given route.
     * This method doesn't perform any sanity checks, and injects power directly into that Pipe.
     *
     * @param route        the route
     * @param energyAmount the amount of energy to send
     */
    protected abstract void sendPowerToPipe(ExitRoute route, double energyAmount);

    private void sendPowerLaserPackets(IRouter sourceRouter, IRouter destinationRouter, ForgeDirection exitOrientation,
                                       boolean addBall) {
        if (sourceRouter == destinationRouter) {
            return;
        }
        LinkedList<Triplet<IRouter, ForgeDirection, Boolean>> todo = new LinkedList<>();
        todo.add(new Triplet<>(sourceRouter, exitOrientation, addBall));
        while (!todo.isEmpty()) {
            Triplet<IRouter, ForgeDirection, Boolean> part = todo.pollFirst();
            List<ExitRoute> exits = part.getValue1().getRoutersOnSide(part.getValue2());
            for (ExitRoute exit : exits) {
                if (exit.containsFlag(PipeRoutingConnectionType.canPowerSubSystemFrom)) { // Find only result (caused by
                    // only straight connections)
                    int distance = part.getValue1().getDistanceToNextPowerPipe(exit.exitOrientation);
                    CoreRoutedPipe pipe = part.getValue1().getPipe();
                    if (pipe != null && pipe.isInitialized()) {
                        pipe.container
                            .addLaser(exit.exitOrientation, distance, getLaserColor(), false, part.getValue3());
                    }
                    IRouter nextRouter = exit.destination; // Use new sourceRouter
                    if (nextRouter == destinationRouter) {
                        return;
                    }
                    outerRouters:
                    for (ExitRoute newExit : nextRouter.getDistanceTo(destinationRouter)) {
                        if (newExit.containsFlag(PipeRoutingConnectionType.canPowerSubSystemFrom)) {
                            for (IFilter filter : newExit.filters) {
                                if (filter.blockPower()) {
                                    continue outerRouters;
                                }
                            }
                            todo.addLast(
                                new Triplet<>(
                                    nextRouter,
                                    newExit.exitOrientation,
                                    newExit.exitOrientation != exit.exitOrientation));
                        }
                    }
                }
            }
        }
    }

    @CCCommand(description = "Returns the color for the power provided by this power provider")
    protected abstract int getLaserColor();

    @Override
    @CCCommand(description = "Returns the max. amount of storable power")
    public double getMaxEnergy() {
        return maxEnergy;
    }

    /**
     * @return The current amount of stored power
     */
    @CCCommand(description = "Returns the current amount of stored power")
    public double getCurrentEnergy() {
        return currentEnergy;
    }

    /**
     * @return how much power is missing until this provider is full
     */
    @CCCommand(description = "Returns how much power is missing until this provider is full")
    public double getDemandedEnergy() {
        return getMaxEnergy() - getCurrentEnergy();
    }

    @Override
    @CCCommand(description = "Returns the power type stored in this power provider")
    public abstract String getBrand();

    @Override
    public void invalidate() {
        super.invalidate();
        if (MainProxy.isClient(getWorld())) {
            LogisticsHUDRenderer.instance().remove(this);
        }
    }

    @Override
    public void validate() {
        super.validate();
        if (MainProxy.isClient(getWorld())) {
            initialized = false;
        }
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        if (MainProxy.isClient(getWorld())) {
            LogisticsHUDRenderer.instance().remove(this);
        }
    }

    /**
     * Checks if the item stack can be placed in the inventory
     *
     * @param itemStack the item stack
     * @return true if the item stack can be placed, otherwise false
     */
    public abstract boolean checkSlot(int SlotId, ItemStack itemStack);

    @Override
    public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
        return new int[1];
    }

    @Override
    public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_, int p_102007_3_) {
        for (int i = 0; i < 9; i++) {
            if (checkSlot(i, p_102007_2_)) return true;
        }
        return false;
    }

    @Override
    public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_, int p_102008_3_) {
        return false;
    }

    @Override
    public int getSizeInventory() {
        return inventory.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int slotIn) {
        return inventory.getStackInSlot(slotIn);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return inventory.decrStackSize(index, count);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        return inventory.getStackInSlotOnClosing(index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        inventory.setInventorySlotContents(index, stack);
    }

    @Override
    public String getInventoryName() {
        return inventory.getInventoryName();
    }

    @Override
    public boolean hasCustomInventoryName() {
        return true;
    }

    @Override
    public int getInventoryStackLimit() {
        return inventory.getInventoryStackLimit();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return inventory.isUseableByPlayer(player);
    }

    @Override
    public void openInventory() {
        inventory.openInventory();
    }

    @Override
    public void closeInventory() {
        inventory.closeInventory();
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return checkSlot(index, stack);
    }

    @Desugar
    protected record PowerOrder(int destinationID, double requestAmount) {
    }

    @Override
    public void requestPower(int destination, double amount) {
        orders.add(new PowerOrder(destination, amount));
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        currentEnergy = nbt.getDouble("currentEnergy");
        maxEnergy = nbt.getDouble("maxEnergy");
        inventory.readFromNBT(nbt);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setDouble("currentEnergy", currentEnergy);
        nbt.setDouble("maxEnergy", maxEnergy);
        inventory.writeToNBT(nbt);
    }

    @Override
    public IHeadUpDisplayRenderer getRenderer() {
        return HUD;
    }

    @Override
    public int getX() {
        return xCoord;
    }

    @Override
    public int getY() {
        return yCoord;
    }

    @Override
    public int getZ() {
        return zCoord;
    }

    @Override
    public World getWorld() {
        return getWorldObj();
    }

    @Override
    public void startWatching() {
        MainProxy.sendPacketToServer(
            PacketHandler.getPacket(HUDStartBlockWatchingPacket.class).setPosX(getX()).setPosY(getY())
                .setPosZ(getZ()));
    }

    @Override
    public void stopWatching() {
        MainProxy.sendPacketToServer(
            PacketHandler.getPacket(HUDStopBlockWatchingPacket.class).setPosX(getX()).setPosY(getY())
                .setPosZ(getZ()));
    }

    @Override
    public void playerStartWatching(EntityPlayer player) {
        watcherList.add(player);
        updateClientSend();
    }

    @Override
    public void playerStopWatching(EntityPlayer player) {
        watcherList.remove(player);
    }

    @Override
    public boolean isHUDExistent() {
        return getWorld().getTileEntity(xCoord, yCoord, zCoord) == this;
    }

    @Override
    public void guiOpenedByPlayer(EntityPlayer player) {
        guiListener.add(player);
        updateClientSend();
    }

    @Override
    public void guiClosedByPlayer(EntityPlayer player) {
        guiListener.remove(player);
    }

    public void updateClientSend() {
        var pack = PacketHandler.getPacket(PowerProviderLevel.class)
            .setMaxEnergy(maxEnergy)
            .setStoredEnergy(currentEnergy)
            .setAverageIO(averageIO)
            .setTilePos(this);

        MainProxy.sendToPlayerList(pack, guiListener);
        MainProxy.sendToPlayerList(pack, watcherList);
    }

    @Override
    public void func_145828_a(CrashReportCategory par1CrashReportCategory) {
        super.func_145828_a(par1CrashReportCategory);
        par1CrashReportCategory.addCrashSection("LP-Version", Tags.VERSION);
    }

    /**
     * Updates the client side with the data from the server side
     *
     * @param updatePackage the update package send from the server
     */
    public void updateClientReceive(PowerProviderLevel updatePackage) {
        if (MainProxy.isServer(getWorld())) return;
        currentEnergy = updatePackage.getStoredEnergy();
        maxEnergy = updatePackage.getMaxEnergy();
        averageIO = updatePackage.getAverageIO();
    }

    @Override
    public int getChargeState() {
        return (int) Math.min(100, 100.0 * currentEnergy / getMaxEnergy());
    }

    @Override
    public boolean isHUDInvalid() {
        return isInvalid();
    }

    @Override
    public CoordinatesGuiProvider getGuiProvider() {
        return NewGuiHandler.getGui(PowerProviderGui.class);
    }
}
