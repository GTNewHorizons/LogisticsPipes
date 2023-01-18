package logisticspipes.proxy.cofh.subproxies;

import net.minecraftforge.common.util.ForgeDirection;

public interface ICoFHEnergyReceiver {

    int getMaxEnergyStored(ForgeDirection opposite);

    int getEnergyStored(ForgeDirection opposite);

    boolean canConnectEnergy(ForgeDirection opposite);

    int receiveEnergy(ForgeDirection opposite, int i, boolean b);
}
