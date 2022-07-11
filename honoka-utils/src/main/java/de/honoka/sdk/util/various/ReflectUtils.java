package de.honoka.sdk.util.various;

import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 便于进行反射操作的工具类
 */
public class ReflectUtils {

    /**
     * 获取某个定义的字段，并调整可访问性，默认移除final修饰符（如果有）
     */
    @SneakyThrows(NoSuchFieldException.class)
    public static Field getField(Class<?> clazz, String fieldName) {
        Field f = clazz.getDeclaredField(fieldName);
        f.setAccessible(true);
        removeFinalModifier(f);
        return f;
    }

    /**
     * 从某个对象的类或父类中获取字段，并调整可访问性
     */
    public static Field getField(Object obj, String fieldName) {
        Class<?> clazz = obj.getClass();
        for(; ; ) {
            try {
                return getField(clazz, fieldName);
            } catch(Exception e) {
                clazz = clazz.getSuperclass();
                if(clazz == null) throw e;
            }
        }
    }

    /**
     * 获取某个定义的方法，并调整可访问性
     */
    @SneakyThrows(NoSuchMethodException.class)
    public static Method getMethod(Class<?> clazz, String methodName,
                                   Class<?>... parameterTypes) {
        Method m = clazz.getDeclaredMethod(methodName, parameterTypes);
        m.setAccessible(true);
        return m;
    }

    /**
     * 从某个对象的类或父类中获取方法，并调整可访问性
     */
    public static Method getMethod(Object obj, String methodName,
                                   Class<?>... parameterTypes) {
        Class<?> clazz = obj.getClass();
        for(; ; ) {
            try {
                return getMethod(clazz, methodName, parameterTypes);
            } catch(Exception e) {
                clazz = clazz.getSuperclass();
                if(clazz == null) throw e;
            }
        }
    }

    /**
     * 获取某个对象的一个成员的值
     */
    @SneakyThrows
    public static Object getFieldValue(Object obj, String fieldName) {
        Field f = getField(obj, fieldName);
        return f.get(obj);
    }

    /**
     * 获取static字段的值
     */
    @SneakyThrows
    public static Object getFieldValue(Class<?> clazz, String fieldName) {
        Field f = getField(clazz, fieldName);
        return f.get(null);
    }

    /**
     * 设置某个对象的一个成员的值
     */
    @SneakyThrows
    public static void setFieldValue(Object obj, String fieldName,
                                     Object value) {
        Field f = getField(obj, fieldName);
        f.set(obj, value);
    }

    /**
     * 设置static字段的值
     */
    @SneakyThrows
    public static void setFieldValue(Class<?> clazz, String fieldName,
                                     Object value) {
        Field f = getField(clazz, fieldName);
        f.set(null, value);
    }

    /**
     * 移除field上的final修饰符（如果有）
     */
    @SneakyThrows
    private static void removeFinalModifier(Field f) {
        if(!Modifier.isFinal(f.getModifiers())) return;
        Field modifiersField = f.getClass().getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
    }

    private static Class<?>[] getParameterType(Object[] args) {
        Class<?>[] parameterType = new Class<?>[args.length];
        for(int i = 0; i < args.length; i++) {
            parameterType[i] = args[i].getClass();
        }
        return parameterType;
    }

    /**
     * 调用某个方法，自动推断要调用的方法的参数类型列表
     */
    @SneakyThrows
    public static Object invokeMethod(Object obj, String methodName,
                                      Object... args) {
        return invokeMethod(obj, methodName, getParameterType(args), args);
    }

    @SneakyThrows
    public static Object invokeMethod(Object obj, String methodName,
                                      Class<?>[] parameterType,
                                      Object... args) {
        Method m = getMethod(obj, methodName, parameterType);
        return m.invoke(obj, args);
    }

    @SneakyThrows
    public static Object invokeMethod(Class<?> clazz, String methodName,
                                      Object... args) {
        return invokeMethod(clazz, methodName, getParameterType(args), args);
    }

    @SneakyThrows
    public static Object invokeMethod(Class<?> clazz, String methodName,
                                      Class<?>[] parameterType,
                                      Object... args) {
        Method m = getMethod(clazz, methodName, parameterType);
        return m.invoke(null, args);
    }
}
