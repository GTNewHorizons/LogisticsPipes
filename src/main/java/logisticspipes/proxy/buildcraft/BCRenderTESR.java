package logisticspipes.proxy.buildcraft;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

import buildcraft.transport.TileGenericPipe;
import buildcraft.transport.render.PipeRendererTESR;
import logisticspipes.pipes.basic.LogisticsTileGenericPipe;
import logisticspipes.proxy.buildcraft.subproxies.IBCRenderTESR;
import lombok.SneakyThrows;

public class BCRenderTESR implements IBCRenderTESR {

    private final MethodHandle renderGatesWires;
    private final MethodHandle renderPluggables;

    @SneakyThrows(Exception.class)
    BCRenderTESR() {
        MethodHandles.Lookup lookup = MethodHandles.lookup();

        Method renderGatesWiresMethod = PipeRendererTESR.class.getDeclaredMethod(
                "renderGatesWires",
                new Class[] { TileGenericPipe.class, double.class, double.class, double.class });
        renderGatesWiresMethod.setAccessible(true);
        renderGatesWires = lookup.unreflect(renderGatesWiresMethod);

        Method renderPluggablesMethod = PipeRendererTESR.class.getDeclaredMethod(
                "renderPluggables",
                new Class[] { TileGenericPipe.class, double.class, double.class, double.class });
        renderPluggablesMethod.setAccessible(true);
        renderPluggables = lookup.unreflect(renderPluggablesMethod);
    }

    @Override
    @SneakyThrows(Throwable.class)
    public void renderWires(LogisticsTileGenericPipe pipe, double x, double y, double z) {
        TileGenericPipe tgPipe = (TileGenericPipe) pipe.tilePart.getOriginal();
        renderGatesWires.invokeExact(PipeRendererTESR.INSTANCE, tgPipe, x, y, z);
    }

    @Override
    @SneakyThrows(Throwable.class)
    public void dynamicRenderPluggables(LogisticsTileGenericPipe pipe, double x, double y, double z) {
        TileGenericPipe tgPipe = (TileGenericPipe) pipe.tilePart.getOriginal();
        renderPluggables.invokeExact(PipeRendererTESR.INSTANCE, tgPipe, x, y, z);
    }
}
