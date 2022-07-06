package de.honoka.util.various;

import de.honoka.util.code.ThrowsRunnable;
import lombok.SneakyThrows;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * 用于忽略指定类型的异常，多次尝试执行一段代码
 */
public class Retrier {

    /**
     * 默认尝试次数
     */
    protected int defaultRetryingTimes = 3;

    /**
     * 要忽略的异常类型集合
     */
    private final List<Class<? extends Throwable>> ignoredThrowableTypes;

    public Retrier(List<Class<? extends Throwable>> types,
                   int defaultRetryingTimes) {
        this(types);
        this.defaultRetryingTimes = defaultRetryingTimes;
    }

    public Retrier(Class<? extends Throwable>... types) {
        this(Arrays.asList(types));
    }

    public Retrier(List<Class<? extends Throwable>> types) {
        ignoredThrowableTypes = types;
    }

    //默认忽略Throwable
    public Retrier() {
        this(Collections.singletonList(Throwable.class));
    }

    public int getDefaultRetryingTimes() {
        return defaultRetryingTimes;
    }

    public void setDefaultRetryingTimes(int defaultRetryingTimes) {
        this.defaultRetryingTimes = defaultRetryingTimes;
    }

    /**
     * 指定尝试次数，多次尝试执行一段代码，返回指定类型的返回值
     */
    @SneakyThrows
    public <T> T tryCode(int times, Callable<T> callable) {
        outerLoop:
        for(int i = 1; ; i++) {        //i表示第几次尝试
            try {
                return callable.call();
            } catch(Throwable t) {
                //达到最大尝试次数，无条件抛出
                if(i >= times) throw t;
                //判断是否是要忽略的异常类型，如果是则忽略并进行下一次尝试，不是则抛出
                for(Class<?> type : ignoredThrowableTypes) {
                    //捕获类型是否是要忽略类型的子类
                    if(type.isAssignableFrom(t.getClass())) continue outerLoop;
                }
                throw t;
            }
        }
    }

    /**
     * 使用默认尝试次数，多次尝试执行一段代码，返回指定类型的返回值
     */
    public <T> T tryCode(Callable<T> callable) {
        return tryCode(defaultRetryingTimes, callable);
    }

    /**
     * 指定尝试次数，多次尝试执行一段代码，不返回值
     */
    public void tryCode(int times, ThrowsRunnable runnable) {
        tryCode(times, () -> {
            runnable.run();
            return null;
        });
    }

    /**
     * 使用默认尝试次数，多次尝试执行一段代码，不返回值
     */
    public void tryCode(ThrowsRunnable runnable) {
        tryCode(defaultRetryingTimes, runnable);
    }
}
