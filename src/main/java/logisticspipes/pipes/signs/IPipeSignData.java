package logisticspipes.pipes.signs;

import logisticspipes.renderer.LogisticsRenderPipe;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface IPipeSignData {

    @SideOnly(Side.CLIENT)
    boolean isListCompatible(LogisticsRenderPipe render);
}
