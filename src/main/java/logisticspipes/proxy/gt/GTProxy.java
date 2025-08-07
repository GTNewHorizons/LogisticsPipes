package logisticspipes.proxy.gt;

import gregtech.api.metatileentity.BaseMetaTileEntity;
import logisticspipes.proxy.interfaces.IEUProxy;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import tectech.thing.metaTileEntity.hatch.MTEHatchEnergyTunnel;

public class GTProxy implements IEUProxy {

    @Override
    public boolean acceptsEnergyFrom(TileEntity sink, TileEntity source, ForgeDirection sinkSide) {
        if (sink instanceof BaseMetaTileEntity baseMetaTileEntity) {
            return baseMetaTileEntity.acceptsEnergyFrom(source, sinkSide);
        }
        return false;
    }

    @Override
    public boolean isEnergySink(TileEntity tileEntity) {
        if (tileEntity instanceof BaseMetaTileEntity baseMetaTileEntity) {
            return baseMetaTileEntity.isEnetInput();
        }
        return false;
    }

    @Override
    public double demandedEnergyUnits(TileEntity sink) {
        if (sink instanceof BaseMetaTileEntity baseMetaTileEntity) {
            return baseMetaTileEntity.demandedEnergyUnits();
        }
        return 0;
    }

    @Override
    public long maxInputAmperage(TileEntity sink) {
        if (sink instanceof BaseMetaTileEntity baseMetaTileEntity) {
           return baseMetaTileEntity.getInputAmperage();
        }
        return 0L;
    }

    @Override
    public long injectEnergyUnits(TileEntity sink, ForgeDirection sinkSide, long amount, long amperage) {
        if (sink instanceof BaseMetaTileEntity baseMetaTileEntity) {
            return baseMetaTileEntity.injectEnergyUnits(sinkSide, amount, amperage) * amount;
        }
        return 0;
    }
}
