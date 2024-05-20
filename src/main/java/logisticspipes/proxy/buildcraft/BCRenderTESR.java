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

    private static final MethodHandle renderGatesWires;
    private static final MethodHandle renderPluggables;

    static {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        renderGatesWires = getHandle(lookup, "renderGatesWires");
        renderPluggables = getHandle(lookup, "renderPluggables");
    }

    private static MethodHandle getHandle(MethodHandles.Lookup lookup, String methodName) {
        try {
            Method method = PipeRendererTESR.class.getDeclaredMethod(
                    methodName,
                    new Class[] { TileGenericPipe.class, double.class, double.class, double.class });
            method.setAccessible(true);
            return lookup.unreflect(method);
        } catch (Exception ignored) {
            return null;
        }
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
