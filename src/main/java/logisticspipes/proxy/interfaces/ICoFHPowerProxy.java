package logisticspipes.proxy.interfaces;

import net.minecraft.tileentity.TileEntity;

import logisticspipes.proxy.cofh.subproxies.ICoFHEnergyReceiver;
import logisticspipes.proxy.cofh.subproxies.ICoFHEnergyStorage;

public interface ICoFHPowerProxy {

    boolean isEnergyReceiver(TileEntity tile);

    ICoFHEnergyReceiver getEnergyReceiver(TileEntity tile);

    void addCraftingRecipes(ICraftingParts parts);

    ICoFHEnergyStorage getEnergyStorage(int i);

    boolean isAvailable();
}
