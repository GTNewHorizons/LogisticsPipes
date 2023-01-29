package logisticspipes.proxy.interfaces;

import logisticspipes.proxy.cofh.subproxies.ICoFHEnergyReceiver;
import logisticspipes.proxy.cofh.subproxies.ICoFHEnergyStorage;

import net.minecraft.tileentity.TileEntity;

public interface ICoFHPowerProxy {

    boolean isEnergyReceiver(TileEntity tile);

    ICoFHEnergyReceiver getEnergyReceiver(TileEntity tile);

    void addCraftingRecipes(ICraftingParts parts);

    ICoFHEnergyStorage getEnergyStorage(int i);

    boolean isAvailable();
}
