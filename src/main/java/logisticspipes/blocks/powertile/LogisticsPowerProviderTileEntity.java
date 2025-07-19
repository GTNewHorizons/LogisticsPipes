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
import logisticspipes.utils.tuples.Pair;
import logisticspipes.utils.tuples.Triplet;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@CCType(name = "LogisticsPowerProvider")
public abstract class LogisticsPowerProviderTileEntity extends LogisticsSolidTileEntity
    implements IGuiTileEntity, ISubSystemPowerProvider, IPowerLevelDisplay, IGuiOpenControler,
    IHeadUpDisplayBlockRendererProvider, IBlockWatchingHandler {

    public static final int BC_COLOR = 0x00ffff;
    public static final int RF_COLOR = 0xff0000;
    public static final int IC2_COLOR = 0xffff00;

    // true if it needs more power, turns off at full, turns on at 50%.
    public boolean needMorePowerTriggerCheck = true;

    protected List<PowerOrder> orders = new ArrayList<>();
    protected List<Pair<CoreRoutedPipe, ForgeDirection>> adjacentPipes = new ArrayList<>();

    protected long internalStorage = 0;
    private float lastUpdateStorage = 0;
    protected int maxMode = 1;

    private final PlayerCollectionList guiListener = new PlayerCollectionList();
    private final PlayerCollectionList watcherList = new PlayerCollectionList();
    private final IHeadUpDisplayRenderer HUD;
    private boolean initialized = false;
    WorldUtil worldUtil = null;


    protected LogisticsPowerProviderTileEntity() {
        HUD = new HUDPowerLevel(this);
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (!initialized) {
            if (MainProxy.isClient(getWorld())) {
                LogisticsHUDRenderer.instance().add(this);
            }
            worldUtil = new WorldUtil(getWorldObj(), getX(), getY(), getZ());
            initialized = true;
        }

        updateAdjacentPipes();

        if (!adjacentPipes.isEmpty())
            handleOrders();

        orders.clear();
        if (MainProxy.isServer(worldObj)) {
            if (internalStorage != lastUpdateStorage) {
                updateClients();
                lastUpdateStorage = internalStorage;
            }
        }
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

    private void handleOrders() {
        orders:
        for (PowerOrder order : orders) {
            long toSend = Math.min(order.requestAmount(), internalStorage);
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
                    internalStorage -= toSend;
                    sendPowerToPipe(route, toSend);

                    break orders;
                }
            }
        }
    }

    /**
     * Sends power to the destination of the given route.
     * This method doesn't perform any sanity checks, and injects power directly into that Pipe.
     *
     * @param route the route
     * @param energyAmount the amount of energy to send
     */
    protected abstract void sendPowerToPipe( ExitRoute route, float energyAmount);

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

    protected abstract float getMaxProvidePerTick();

    @CCCommand(description = "Returns the color for the power provided by this power provider")
    protected abstract int getLaserColor();

    @Override
    @CCCommand(description = "Returns the max. amount of storable power")
    public abstract int getMaxStorage();

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

    @Desugar
    protected record PowerOrder(int destinationID, long requestAmount) {
    }

    @Override
    public void requestPower(int destination, long amount) {
        orders.add(new PowerOrder(destination, amount));
    }

    @Override
    @CCCommand(description = "Returns the current power level for this power provider")
    public float getPowerLevel() {
        return lastUpdateStorage;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        internalStorage = nbt.getLong("internalStorage");
        maxMode = nbt.getInteger("maxMode");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setLong("internalStorage", internalStorage);
        nbt.setInteger("maxMode", maxMode);
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
        updateClients();
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
        updateClients();
    }

    @Override
    public void guiClosedByPlayer(EntityPlayer player) {
        guiListener.remove(player);
    }

    public void updateClients() {
        MainProxy.sendToPlayerList(
            PacketHandler.getPacket(PowerProviderLevel.class).setFloat(internalStorage).setTilePos(this),
            guiListener);
        MainProxy.sendToPlayerList(
            PacketHandler.getPacket(PowerProviderLevel.class).setFloat(internalStorage).setTilePos(this),
            watcherList);
    }

    @Override
    public void func_145828_a(CrashReportCategory par1CrashReportCategory) {
        super.func_145828_a(par1CrashReportCategory);
        par1CrashReportCategory.addCrashSection("LP-Version", Tags.VERSION);
    }

    public void handlePowerPacket(float float1) {
        if (MainProxy.isClient(getWorld())) {
            internalStorage = (long) float1;
        }
    }

    @Override
    public int getChargeState() {
        return (int) Math.min(100F, internalStorage * 100 / getMaxStorage());
    }

    @Override
    public int getDisplayPowerLevel() {
        return Math.round(internalStorage);
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
