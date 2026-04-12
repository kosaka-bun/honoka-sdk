package de.honoka.sdk.util.various;

import lombok.SneakyThrows;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用于简化代码的工具类
 */
public class CodeUtils {

    /**
     * 不受检的线程休眠方法
     */
    @SneakyThrows
    public static void threadSleep(long millis) {
        Thread.sleep(millis);
    }

    /**
     * 输出所有系统配置
     */
    public static void printSystemProperties() {
        List<Map.Entry<Object, Object>> props = System.getProperties()
            .entrySet()
            .stream()
            .sorted((o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.toString(), o2.toString()))
            .collect(Collectors.toList());
        for(Map.Entry<Object, Object> prop : props) {
            System.out.println(prop.getKey().toString() + "=" + prop.getValue());
            System.out.println();
        }
    }

    @SneakyThrows
    public static void sneakyThrows(Throwable t) {
        throw t;
    }

    public static Class<?> getCallerClass() {
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        if(stackTrace.length < 3) return null;
        try {
            for(int i = 2; i < stackTrace.length; i++) {
                StackTraceElement element = stackTrace[i];
                if(element.getMethodName().endsWith("$default")) continue;
                return Class.forName(element.getClassName());
            }
            return null;
        } catch(Throwable t) {
            return null;
        }
    }

    /**
     * 忽略异常执行一段代码
     */
    public static void runCatching(boolean printStackTrace, ThrowsRunnable action) {
        try {
            action.throwsRun();
        } catch(Throwable t) {
            if(printStackTrace) {
                t.printStackTrace();
            }
        }
    }

    public static void runCatching(ThrowsRunnable action) {
        runCatching(false, action);
    }

    /**
     * 控制台可视化执行一段代码
     */
    public static void tryActionVisiable(String name, ThrowsRunnable action) {
        System.out.println("开始执行" + name + "……");
        try {
            action.throwsRun();
            System.out.println(name + "执行完成");
        } catch(Throwable t) {
            System.err.println(name + "未成功执行，错误信息如下：");
            t.printStackTrace();
        }
    }
}
