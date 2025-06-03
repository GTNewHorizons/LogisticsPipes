package logisticspipes.asm;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

import logisticspipes.LPConstants;
import logisticspipes.proxy.computers.wrapper.CCObjectWrapper;
import logisticspipes.proxy.opencomputers.asm.ClassCreator;

public class LogisticsPipesClassInjector implements IClassTransformer {

    private final Field fResourceCache;

    public LogisticsPipesClassInjector() throws NoSuchFieldException, SecurityException {
        fResourceCache = LaunchClassLoader.class.getDeclaredField("resourceCache");
        fResourceCache.setAccessible(true);
    }

    @Override
    @SuppressWarnings("unchecked")
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (bytes != null) {
            return bytes;
        }
        try {
            if (name.startsWith("logisticspipes.proxy.opencomputers.asm.BaseWrapperClass$")
                    && name.endsWith("$OpenComputersWrapper")) {
                String correctName = name.substring(56, name.length() - 21);
                Class<?> clazz = Launch.classLoader.findClass(correctName);
                bytes = ClassCreator
                        .getWrappedClassAsBytes(CCObjectWrapper.getWrapperInformation(clazz), clazz.getName());
                Set<String> set = new TreeSet<>();
                set.add(name);
                Launch.classLoader.clearNegativeEntries(set);
                Map<String, byte[]> map = (Map<String, byte[]>) fResourceCache.get(Launch.classLoader);
                map.put(name, bytes);
                return bytes;
            }
        } catch (Exception e) {
            if (LPConstants.DEBUG) { // For better Debugging
                e.printStackTrace();
                return bytes;
            }
            throw new RuntimeException(e);
        }
        return bytes;
    }
}
