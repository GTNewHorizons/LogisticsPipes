package logisticspipes.proxy.enderio;

import logisticspipes.proxy.interfaces.IGenericProgressProvider;

import net.minecraft.tileentity.TileEntity;

import crazypants.enderio.machine.AbstractPoweredTaskEntity;

public class EnderIOProgressProvider implements IGenericProgressProvider {

    @Override
    public boolean isType(TileEntity tile) {
        return tile instanceof AbstractPoweredTaskEntity;
    }

    @Override
    public byte getProgress(TileEntity tile) {
        return (byte) Math.max(0, Math.min(((AbstractPoweredTaskEntity) tile).getProgress() * 100, 100));
    }
}
