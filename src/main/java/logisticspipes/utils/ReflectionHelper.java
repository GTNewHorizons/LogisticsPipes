package logisticspipes.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import logisticspipes.utils.tuples.Triplet;

public class ReflectionHelper {

    private static final Map<Triplet<Class<?>, String, List<Class<?>>>, Method> methodCache = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> T invokePrivateMethod(Class<T> type, Class<?> clazz, Object target, String name,
            Class<?>[] classes, Object[] objects) throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Triplet<Class<?>, String, List<Class<?>>> key = new Triplet<>(clazz, name, Arrays.asList(classes));
        Method method = ReflectionHelper.methodCache.get(key);
        if (method == null) {
            try {
                method = clazz.getDeclaredMethod(name, classes);
            } catch (NoSuchMethodException e1) {
                try {
                    method = clazz.getMethod(name, classes);
                } catch (NoSuchMethodException e2) {
                    method = clazz.getDeclaredMethod(name, classes);
                }
            }
            method.setAccessible(true);
            ReflectionHelper.methodCache.put(key, method);
        }
        Object result = method.invoke(target, objects);
        return (T) result;
    }

    public static <T> T invokePrivateMethodCatched(Class<T> type, Class<?> clazz, Object target, String name,
            Class<?>[] classes, Object[] objects) {
        try {
            return ReflectionHelper.invokePrivateMethod(type, clazz, target, name, classes, objects);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getPrivateField(Class<T> resultClass, Class<?> objectClass, String name, Object object)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field field = objectClass.getDeclaredField(name);
        field.setAccessible(true);
        Object result = field.get(object);
        return (T) result;
    }
}
