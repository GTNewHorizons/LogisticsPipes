package logisticspipes.blocks.powertile;

import logisticspipes.routing.ExitRoute;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.common.Optional;
import logisticspipes.proxy.MainProxy;
import logisticspipes.proxy.SimpleServiceLocator;
import logisticspipes.proxy.cofh.subproxies.ICoFHEnergyStorage;

@Optional.Interface(modid = "CoFHAPI|energy", iface = "cofh.api.energy.IEnergyHandler")
public class LogisticsRFPowerProviderTileEntity extends LogisticsPowerProviderTileEntity implements IEnergyHandler {

    public static final int BASE_STORAGE = 10000000;

    private final ICoFHEnergyStorage storage;

    public LogisticsRFPowerProviderTileEntity() {
        storage = SimpleServiceLocator.cofhPowerProxy.getEnergyStorage(10000);
    }

    @Override
    protected double getMaxEnergyIO() {
        return BASE_STORAGE;
    }

    private void addStoredRF() {
        int space = (int) getDemandedEnergy();
        int available = (storage.extractEnergy(space, true));
        if (available > 0) {
            if (storage.extractEnergy(available, false) == available) {
                currentEnergy += Math.min(available, getMaxEnergy());

            }
        }
    }


    @Override
    public void updateEntity() {
        super.updateEntity();
        if (MainProxy.isServer(worldObj)) {
            if (getDemandedEnergy() > 0) {
                addStoredRF();
            }
        }
    }

    @Override
    @Optional.Method(modid = "CoFHAPI|energy")
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        return storage.receiveEnergy(maxReceive, simulate);
    }

    @Override
    @Optional.Method(modid = "CoFHAPI|energy")
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        return storage.extractEnergy(maxExtract, simulate);
    }

    @Override
    @Optional.Method(modid = "CoFHAPI|energy")
    public boolean canConnectEnergy(ForgeDirection from) {
        return true;
    }

    @Override
    @Optional.Method(modid = "CoFHAPI|energy")
    public int getEnergyStored(ForgeDirection from) {
        return storage.getEnergyStored();
    }

    @Override
    @Optional.Method(modid = "CoFHAPI|energy")
    public int getMaxEnergyStored(ForgeDirection from) {
        return storage.getMaxEnergyStored();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        storage.readFromNBT(nbt);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        storage.writeToNBT(nbt);
    }

    @Override
    public String getBrand() {
        return "RF";
    }

    @Override
    protected void sendPowerToPipe(ExitRoute route, double energyAmount) {
        route.destination.getPipe().handleRFPowerArival((float) energyAmount);
    }

    @Override
    protected int getLaserColor() {
        return LogisticsPowerProviderTileEntity.RF_COLOR;
    }
}
