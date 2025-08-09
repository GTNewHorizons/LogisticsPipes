package logisticspipes.blocks.powertile;

import cpw.mods.fml.common.Optional;
import gregtech.api.items.MetaBaseItem;
import gregtech.api.util.GTModHandler;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.item.IElectricItem;
import logisticspipes.proxy.MainProxy;
import logisticspipes.proxy.SimpleServiceLocator;
import logisticspipes.renderer.LogisticsHUDRenderer;
import logisticspipes.routing.ExitRoute;
import logisticspipes.utils.item.ItemIdentifierStack;
import logisticspipes.utils.tuples.Pair;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Arrays;
import java.util.Objects;

@Optional.Interface(modid = "IC2", iface = "ic2.api.energy.tile.IEnergySink")
public class LogisticsIC2PowerProviderTileEntity extends LogisticsPowerProviderTileEntity implements IEnergySink {

    public static final double BASE_STORAGE = 32000.0;
    public static final double BASE_IO_ENERGY = 32.0;

    protected double maxIOEnergy = BASE_IO_ENERGY;
    private boolean addedToEnergyNet = false;
    private boolean init = false;
    private double energyInputThisTick = 0;

    public LogisticsIC2PowerProviderTileEntity() {
        super();
        maxEnergy = BASE_STORAGE;
    }


    @Override
    protected void updateCapacity() {
        double newCapacity = BASE_STORAGE;
        double newAmperage = BASE_IO_ENERGY;

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            var batteryStack = inventory.getStackInSlot(i);
            if (batteryStack == null) continue;

            var battery = (ic2.api.item.IElectricItem) batteryStack.getItem();
            if (battery == null) continue;

            newCapacity += battery.getMaxCharge(batteryStack);
            newAmperage += battery.getTransferLimit(batteryStack);
        }

        maxEnergy = newCapacity;
        maxIOEnergy = newAmperage;

        if (maxEnergy < currentEnergy)
            currentEnergy = maxEnergy;
    }

    @Override
    protected double getMaxEnergyIO() {
        return maxIOEnergy;
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

        if (MainProxy.isServer(getWorld())) energyInputThisTick = 0;
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

    @Override
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
        // voltage the same value, and not (like i would expect) amount the energy, and voltage the actual amperes.
        // we need to collect the energy in on tick and check that we only accept as much as this block can handle
        // for now we dont do any shenanigans with explosions or overvoltages on LP Power providers or the internal
        // batteries.

        var maxIO = getMaxEnergyIO();
        if (energyInputThisTick + amount > maxIO) {
            amount = maxIO - energyInputThisTick;
        }
        currentEnergy += amount;
        energyInputThisTick += amount;

        addEnergyToBatteries(amount);

        if (currentEnergy > getMaxEnergy()) currentEnergy = getMaxEnergy();

        //return the 'unused' amount of the send energy
        return voltage - amount;
    }

    private void addEnergyToBatteries(double amount) {
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            var aStack = inventory.getStackInSlot(i);
            if (GTModHandler.isElectricItem(aStack)) {

                if (aStack.getItem() instanceof MetaBaseItem metaBaseItem) {
                    Long[] stats = metaBaseItem.getElectricStats(aStack);
                    var charge = Math.min(stats[1],amount);
                    metaBaseItem.charge(aStack, charge, metaBaseItem.getTier(aStack), false, false);

                    System.out.println(metaBaseItem.getCharge(aStack));
                } else if (aStack.getItem() instanceof IElectricItem) {
                    var stored = ic2.api.item.ElectricItem.manager.getCharge(aStack);
                    var maxCharge = ((IElectricItem) aStack.getItem()).getMaxCharge(aStack);
                    System.out.println(stored + " / " + maxCharge);
                }
            }
        }
    }
}
