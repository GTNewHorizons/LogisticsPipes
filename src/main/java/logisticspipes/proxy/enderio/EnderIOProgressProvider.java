package logisticspipes.proxy.enderio;

import net.minecraft.tileentity.TileEntity;

import crazypants.enderio.machine.AbstractPoweredTaskEntity;
import logisticspipes.proxy.interfaces.IGenericProgressProvider;

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
