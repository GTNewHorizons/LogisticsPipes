package logisticspipes.proxy.buildcraft;

import java.lang.reflect.Method;

import buildcraft.transport.TileGenericPipe;
import buildcraft.transport.render.PipeRendererTESR;
import logisticspipes.pipes.basic.LogisticsTileGenericPipe;
import logisticspipes.proxy.buildcraft.subproxies.IBCRenderTESR;
import lombok.SneakyThrows;

public class BCRenderTESR implements IBCRenderTESR {

    private final Method renderGatesWires;
    private final Method renderPluggables;

    @SneakyThrows(Exception.class)
    BCRenderTESR() {
        renderGatesWires = PipeRendererTESR.class.getDeclaredMethod(
                "renderGatesWires",
                new Class[] { TileGenericPipe.class, double.class, double.class, double.class });
        renderGatesWires.setAccessible(true);
        renderPluggables = PipeRendererTESR.class.getDeclaredMethod(
                "renderPluggables",
                new Class[] { TileGenericPipe.class, double.class, double.class, double.class });
        renderPluggables.setAccessible(true);
    }

    @Override
    @SneakyThrows(Exception.class)
    public void renderWires(LogisticsTileGenericPipe pipe, double x, double y, double z) {
        TileGenericPipe tgPipe = (TileGenericPipe) pipe.tilePart.getOriginal();
        renderGatesWires.invoke(PipeRendererTESR.INSTANCE, tgPipe, x, y, z);
    }

    @Override
    @SneakyThrows(Exception.class)
    public void dynamicRenderPluggables(LogisticsTileGenericPipe pipe, double x, double y, double z) {
        TileGenericPipe tgPipe = (TileGenericPipe) pipe.tilePart.getOriginal();
        renderPluggables.invoke(PipeRendererTESR.INSTANCE, tgPipe, x, y, z);
    }
}
