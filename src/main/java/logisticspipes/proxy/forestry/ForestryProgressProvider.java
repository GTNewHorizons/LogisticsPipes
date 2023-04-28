package logisticspipes.proxy.forestry;

import java.lang.reflect.Field;

import net.minecraft.tileentity.TileEntity;

import forestry.core.tiles.TilePowered;
import logisticspipes.proxy.interfaces.IGenericProgressProvider;

public class ForestryProgressProvider implements IGenericProgressProvider {

    private final Field workCounter;

    public ForestryProgressProvider() throws NoSuchFieldException, SecurityException {
        workCounter = TilePowered.class.getDeclaredField("workCounter");
        workCounter.setAccessible(true);
    }

    @Override
    public boolean isType(TileEntity tile) {
        return tile instanceof TilePowered;
    }

    @Override
    public byte getProgress(TileEntity tile) {
        try {
            return (byte) Math.max(0, Math.min((Integer) workCounter.get(tile) * 4, 100));
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
