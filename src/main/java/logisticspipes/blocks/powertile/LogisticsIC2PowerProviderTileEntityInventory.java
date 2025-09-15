package logisticspipes.blocks.powertile;

import logisticspipes.proxy.SimpleServiceLocator;
import logisticspipes.proxy.interfaces.IIC2Proxy;
import logisticspipes.utils.item.SimpleStackInventory;
import net.minecraft.inventory.IInventory;

public class LogisticsIC2PowerProviderTileEntityInventory extends SimpleStackInventory {
    private static final IIC2Proxy IIC_2_PROXY = SimpleServiceLocator.IC2Proxy;
    private LogisticsIC2PowerProviderTileEntity parent;
    private int currentBaseCharge;
    private double currentCharge;
    private double currentCapacity;
    private double maxTransferLimit;
    private double energyInjected;
    private double energyExtracted;

    public LogisticsIC2PowerProviderTileEntityInventory(LogisticsIC2PowerProviderTileEntity tileEntity) {
        super(9, "LogisticsIC2PowerProviderTileEntityInventory", 1);
        parent = tileEntity;
        addListener(this::onInventoryChanged);
    }

    protected void onTick() {
        energyExtracted = energyInjected = 0;
    }

    private void onInventoryChanged(IInventory iInventory) {
        currentCharge = currentBaseCharge;
        currentCapacity = LogisticsIC2PowerProviderTileEntity.BASE_STORAGE;
        maxTransferLimit = LogisticsIC2PowerProviderTileEntity.BASE_IO_ENERGY;

        for (int i = 0; i < getSizeInventory(); i++) {
            var stack = getStackInSlot(i);
            if (stack == null) continue;

            currentCapacity += IIC_2_PROXY.getMaxCharge(stack);
            currentCharge += IIC_2_PROXY.getCurrentCharge(stack);
            maxTransferLimit += IIC_2_PROXY.getVoltage(stack);
        }
    }

    public double chargeBatteries(double amount) {
        var remainingTransfer = maxTransferLimit - energyInjected;
        var chargedAmount = 0.0;

        //charge internal buffer


        //charge additional battery
        for (int i = 0; i < getSizeInventory(); i++) {
            var stack = getStackInSlot(i);
            if (stack == null || amount <= 0) continue;
            double toCharge = Math.min(remainingTransfer, Math.min(amount, IIC_2_PROXY.getMaxCharge(stack) - IIC_2_PROXY.getCurrentCharge(stack)));
            double charged = IIC_2_PROXY.chargeElectricItem(stack, toCharge);
            amount -= charged;
            remainingTransfer -= charged;
            chargedAmount += charged;
            currentCharge += charged;
            energyInjected += charged;
        }
        return chargedAmount;
    }

    public double dechargeBatteries(double amount) {
        var remainingTransfer = maxTransferLimit - energyExtracted;
        var dechargedAmount = 0.0;
        for (int i = 0; i <= 9; i++) {
            var stack = getStackInSlot(i);
            if (stack == null || amount <= 0) continue;
            double toDecharge = Math.min(remainingTransfer, Math.min(amount, IIC_2_PROXY.getCurrentCharge(stack)));
            double decharged = IIC_2_PROXY.dischargeElectricItem(stack, toDecharge);
            amount -= decharged;
            remainingTransfer -= decharged;
            dechargedAmount += decharged;
            currentCharge -= decharged;
            energyExtracted += decharged;
        }
        return dechargedAmount;
    }


    public double getMaxIO() {
        return maxTransferLimit;
    }

    public double getCurrentCharge() {
        return currentCharge;
    }

    public double getCurrentCapacity() {
        return currentCapacity;
    }

    public double getChargePercent() {
        if (currentCapacity <= 0) return 0;
        return currentCharge / currentCapacity;
    }

    public double getEnergyInjected() {
        return energyInjected;
    }

    public double getEnergyExtracted() {
        return energyExtracted;
    }


}
