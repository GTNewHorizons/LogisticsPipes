package logisticspipes.blocks.powertile;

import ic2.api.item.IElectricItem;
import logisticspipes.LogisticsPipes;
import logisticspipes.proxy.SimpleServiceLocator;
import logisticspipes.proxy.interfaces.IIC2Proxy;
import logisticspipes.utils.MathUtil;
import logisticspipes.utils.item.SimpleStackInventory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class LogisticsIC2PowerProviderTileEntityInventory extends SimpleStackInventory {
    private static final IIC2Proxy IIC_2_PROXY = SimpleServiceLocator.IC2Proxy;
    private static final int ENERGY_ITEM_SLOTS = 9;

    @Getter
    @Setter
    @AllArgsConstructor
    private class EnergyItemWrapper {

        public EnergyItemWrapper(ItemStack stack, int index) {
            this.index = index;
            if (stack != null && IIC_2_PROXY.isElectricItem(stack) && stack.getItem() instanceof IElectricItem) {
                this.capacity = IIC_2_PROXY.getMaxCharge(stack);
                this.charge = IIC_2_PROXY.getCurrentCharge(stack);
                this.inIO = IIC_2_PROXY.getVoltage(stack);
                this.outIO = IIC_2_PROXY.getVoltage(stack);
                this.maxIO = IIC_2_PROXY.getVoltage(stack);
            } else {
                LogisticsPipes.log.warn("Found non electric item in LogisticsIC2PowerProviderTileEntity: " + stack);
                this.capacity = 0;
                this.charge = 0;
                this.inIO = 0;
                this.outIO = 0;
                this.maxIO = 0;
            }
        }

        public int index;
        public double capacity;
        public double charge;
        public double inIO;
        public double outIO;
        public double maxIO;

        /**
         * Charges the item by the given amount, up to its capacity and max IO limits.
         *
         * @param amount The amount of energy to charge.
         * @return The amount of energy that was actually accepted.
         */
        public double charge(double amount) {
            if (charge >= capacity) return 0;

            double toCharge = MathUtil.min(amount, capacity - charge, maxIO - inIO);

            //charge the actual item (if its not the internal buffer)
            if (index != -1) toCharge = IIC_2_PROXY.chargeElectricItem(LogisticsIC2PowerProviderTileEntityInventory.this.getStackInSlot(index), toCharge);

            this.charge += toCharge;
            inIO += toCharge;
            return toCharge;

        }

        public double discharge(double amount) {
            if (capacity <= 0) return 0;

            double toDischarge = MathUtil.min(amount, charge, maxIO - outIO);

            //discharge the actual item (if its not the internal buffer)
            if (index != -1) toDischarge = IIC_2_PROXY.dischargeElectricItem(LogisticsIC2PowerProviderTileEntityInventory.this.getStackInSlot(index), toDischarge);

            this.charge -= toDischarge;
            outIO += toDischarge;
            return toDischarge;
        }
    }

    private final EnergyItemWrapper[] energyItems;

    @Getter
    private double currentCapacity;
    @Getter
    private double currentCharge;
    @Getter
    private double maxIO;

    public LogisticsIC2PowerProviderTileEntityInventory() {
        super(ENERGY_ITEM_SLOTS, "LogisticsIC2PowerProviderTileEntityInventory", 1);
        energyItems = new EnergyItemWrapper[ENERGY_ITEM_SLOTS + 1];

        energyItems[ENERGY_ITEM_SLOTS] = new EnergyItemWrapper(
            -1,
            LogisticsIC2PowerProviderTileEntity.BASE_STORAGE,
            0,
            LogisticsIC2PowerProviderTileEntity.BASE_IO_ENERGY,
            LogisticsIC2PowerProviderTileEntity.BASE_IO_ENERGY,
            LogisticsIC2PowerProviderTileEntity.BASE_IO_ENERGY);

        currentCapacity = LogisticsIC2PowerProviderTileEntity.BASE_STORAGE;
        currentCharge = 0;
        maxIO = LogisticsIC2PowerProviderTileEntity.BASE_IO_ENERGY;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound, String prefix) {
        super.writeToNBT(nbttagcompound, prefix);
        nbttagcompound.setInteger(prefix + "currentBaseCharge", (int) energyItems[ENERGY_ITEM_SLOTS].getCharge());
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound, String prefix) {
        NBTTagList nbttaglist = nbttagcompound.getTagList(prefix + "items", nbttagcompound.getId());

        for (int j = 0; j < nbttaglist.tagCount(); ++j) {
            NBTTagCompound nbttagcompound2 = nbttaglist.getCompoundTagAt(j);
            int index = nbttagcompound2.getInteger("index");
            if (index < ENERGY_ITEM_SLOTS) {
                var itemStack = ItemStack.loadItemStackFromNBT(nbttagcompound2);
                setInventorySlotContents(index, itemStack);
                energyItems[index] = new EnergyItemWrapper(itemStack, index);
            } else {
                LogisticsPipes.log.fatal("Trying to add an item to " + index + " but Inventory only has " + ENERGY_ITEM_SLOTS + " slots.");
            }
        }
        energyItems[ENERGY_ITEM_SLOTS].setCharge(nbttagcompound.getInteger(prefix + "currentBaseCharge"));
    }

    /**
     * Resets the in/out IO counters for all energy items. Should be called once per tick.
     * We have to do this, because some energy provider provide energy multiple times per tick to get the correct amperage.
     */
    protected void onTick() {
        for (EnergyItemWrapper energyItem : energyItems) {
            if (energyItem == null) continue;
            energyItem.setInIO(0);
            energyItem.setOutIO(0);
        }
    }

    @Override
    public void clearInventorySlotContents(int i) {
        super.clearInventorySlotContents(i);
        if (energyItems[i] != null) {
            maxIO -= energyItems[i].getMaxIO();
            currentCapacity -= energyItems[i].getCapacity();
            currentCharge -= energyItems[i].getCharge();
            energyItems[i] = null;
        }
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemStack) {
        super.setInventorySlotContents(i, itemStack);
        if (itemStack != null) {
            var newEnergyItem = new EnergyItemWrapper(itemStack, i);
            maxIO += newEnergyItem.getMaxIO();
            currentCapacity += newEnergyItem.getCapacity();
            currentCharge += newEnergyItem.getCharge();
            energyItems[i] = newEnergyItem;
        } else {
            if (energyItems[i] != null) {
                maxIO -= energyItems[i].getMaxIO();
                currentCapacity -= energyItems[i].getCapacity();
                currentCharge -= energyItems[i].getCharge();
            }
            energyItems[i] = null;
        }
    }

    /**
     * Adds energy to the internal batteries. Returns how much energy was accepted.
     *
     * @param amount The amount of energy to add.
     * @return The amount of energy that was accepted.
     */
    public double addEnergy(final double amount) {
        double unused = amount;
        for (var energyItem : energyItems) {
            if (energyItem == null) continue;
            var charged = energyItem.charge(unused);
            unused -= charged;
            currentCharge += charged;
        }
        return amount - unused;
    }

    /**
     * Trys to remove energy from the internal batteries. Returns how much energy was removed.
     *
     * @param amount The amount of energy to remove.
     * @return The amount of energy that was removed.
     */
    public double removeEnergy(final double amount) {
        double unused = amount;
        for (var energyItem : energyItems) {
            if (energyItem == null) continue;
            var removed = energyItem.discharge(amount);
            unused -= removed;
            currentCharge -= removed;
        }

        return amount - unused;
    }
}
