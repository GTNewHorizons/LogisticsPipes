package logisticspipes.asm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.LaunchClassLoader;

import logisticspipes.LPConstants;

public class LogisticsClassTransformer implements IClassTransformer {

    public List<String> interfacesToClearA = new ArrayList<>();
    public List<String> interfacesToClearB = new ArrayList<>();
    private final LaunchClassLoader cl = (LaunchClassLoader) LogisticsClassTransformer.class.getClassLoader();
    private Field negativeResourceCache;
    private Field invalidClasses;

    public LogisticsClassTransformer() {
        try {
            negativeResourceCache = LaunchClassLoader.class.getDeclaredField("negativeResourceCache");
            negativeResourceCache.setAccessible(true);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        try {
            invalidClasses = LaunchClassLoader.class.getDeclaredField("invalidClasses");
            invalidClasses.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        Thread thread = Thread.currentThread();
        if (thread.getName().equals("Minecraft main thread") || thread.getName().equals("main")
                || thread.getName().equals("Server thread")) { // Only clear when called from the main thread to avoid
            // ConcurrentModificationException on start
            clearNegativeInterfaceCache();
        }
        if (bytes == null) {
            return null;
        }
        return ParamProfiler.handleClass(bytes);
    }

    public void clearNegativeInterfaceCache() {
        // Remove previously not found Classes to Fix ClassNotFound Exceptions for Interfaces.
        // TODO remove in future version when everybody starts using a ClassTransformer system for Interfaces.
        if (negativeResourceCache != null) {
            if (!interfacesToClearA.isEmpty()) {
                handleField(negativeResourceCache, interfacesToClearA);
            }
        }
        if (invalidClasses != null) {
            if (!interfacesToClearB.isEmpty()) {
                handleField(invalidClasses, interfacesToClearB);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void handleField(Field field, List<String> toClear) {
        try {
            Set<String> set = (Set<String>) field.get(cl);
            Iterator<String> it = toClear.iterator();
            while (it.hasNext()) {
                String content = it.next();
                if (set.contains(content)) {
                    set.remove(content);
                    it.remove();
                }
            }
        } catch (Exception e) {
            if (LPConstants.DEBUG) { // For better Debugging
                e.printStackTrace();
            }
        }
    }
}
