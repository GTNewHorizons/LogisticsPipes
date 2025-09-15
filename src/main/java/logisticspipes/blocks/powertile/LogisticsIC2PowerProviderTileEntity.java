package logisticspipes.blocks.powertile;

import cpw.mods.fml.common.Optional;
import ic2.api.energy.tile.IEnergySink;
import logisticspipes.proxy.MainProxy;
import logisticspipes.proxy.SimpleServiceLocator;
import logisticspipes.renderer.LogisticsHUDRenderer;
import logisticspipes.routing.ExitRoute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Objects;

@Optional.Interface(modid = "IC2", iface = "ic2.api.energy.tile.IEnergySink")
public class LogisticsIC2PowerProviderTileEntity extends LogisticsPowerProviderTileEntity implements IEnergySink, ISidedInventory {

    private final LogisticsIC2PowerProviderTileEntityInventory inventory;

    public static final int BASE_STORAGE = 32000;
    public static final int BASE_IO_ENERGY = 32;

    private boolean addedToEnergyNet = false;
    private boolean init = false;


    public LogisticsIC2PowerProviderTileEntity() {
        super();
        inventory = new LogisticsIC2PowerProviderTileEntityInventory(this);
    }

    @Override
    protected double getMaxEnergyIO() {
        return BASE_IO_ENERGY + inventory.getMaxIO();
    }

    @Override
    public double getMaxEnergy() {
        return BASE_STORAGE + inventory.getCurrentCapacity();
    }

    @Override
    public double getCurrentEnergy() {
        return inventory.getCurrentCharge();
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (!init) {
            if (!addedToEnergyNet) {
                SimpleServiceLocator.IC2Proxy.registerToEneryNet(this);
                addedToEnergyNet = true;
            }
        }

        if (MainProxy.isServer(getWorld())) {
            inventory.onTick();
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (MainProxy.isClient(getWorld())) {
            LogisticsHUDRenderer.instance().remove(this);
        }
        if (addedToEnergyNet) {
            SimpleServiceLocator.IC2Proxy.unregisterToEneryNet(this);
            addedToEnergyNet = false;
        }
    }

    @Override
    public void validate() {
        super.validate();
        if (MainProxy.isClient(getWorld())) {
            init = false;
        }
        if (!addedToEnergyNet) {
            init = false;
        }
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        if (MainProxy.isClient(getWorld())) {
            LogisticsHUDRenderer.instance().remove(this);
        }
        if (addedToEnergyNet) {
            SimpleServiceLocator.IC2Proxy.unregisterToEneryNet(this);
            addedToEnergyNet = false;
        }
    }

    public boolean checkSlot(int slotId, ItemStack itemStack) {
        if (itemStack.getItem() == null) return false;
        //test if item is a battery
        var item = itemStack.getItem();
        if (!(itemStack.getItem() instanceof ic2.api.item.IElectricItem)) return false;


        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack inventoryStack = inventory.getStackInSlot(i);
            if (inventoryStack == null || inventoryStack.getItem() == null) return true;
            if (Objects.equals(item.getUnlocalizedName(), inventoryStack.getItem().getUnlocalizedName())
                && Objects.equals(item.getDamage(itemStack), inventoryStack.getItem().getDamage(inventoryStack))
                && inventoryStack.stackSize < inventoryStack.getMaxStackSize()) return true;
        }
        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        inventory.readFromNBT(nbt);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        inventory.writeToNBT(nbt);
    }

    @Override
    public String getBrand() {
        return "EU";
    }

    @Override
    protected void sendPowerToPipe(ExitRoute route, double energyAmount) {
        //we need to get the actual route somehow, so we can check it for how much energy will be lost on it.
        //we also need to get the amperage here, so we can calculate loss/meter/amp
        route.destination.getPipe().handleIC2PowerArival(energyAmount);
    }

    @Override
    protected int getLaserColor() {
        return LogisticsPowerProviderTileEntity.IC2_COLOR;
    }

    @Override
    @Optional.Method(modid = "IC2")
    public boolean acceptsEnergyFrom(TileEntity tile, ForgeDirection dir) {
        return true;
    }

    @Override
    @Optional.Method(modid = "IC2")
    public int getSinkTier() {
        return Integer.MAX_VALUE;
    }

    @Override
    @Optional.Method(modid = "IC2")
    public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage) {
        if (MainProxy.isClient(getWorld())) return 0;

        // idk why, but from gt cables we get multiple injections per tick, with amount as the energy provided, and
        // voltage the same value, and not (like i would expect) voltage the energy, and amount the actual amperes.
        // we need to collect the energy in on tick and check that we only accept as much as this block can handle
        // for now we dont do any shenanigans with explosions on LP Power providers or the internal batteries.

        return voltage - inventory.chargeBatteries(voltage);
    }

    // ---- INVENTORY HANLDER ---- //

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

}
