package de.honoka.sdk.util.concurrent;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 用于支持{@link ThreadPoolExecutor}在线程数未达到最大线程数时创建新线程的任务队列。
 * <p>
 * <b>
 * 本类来源于Dubbo（<a href="https://github.com/apache/dubbo/blob/3.3/dubbo-common/src/main
 * /java/org/apache/dubbo/common/threadpool/support/eager/TaskQueue.java">
 * https://github.com/apache/dubbo/blob/3.3/dubbo-common/src/main/java/org/apache/dubbo/common
 * /threadpool/support/eager/TaskQueue.java
 * </a>），并进行了一些修改。
 * </b>
 */
@Setter(AccessLevel.PACKAGE)
@Getter
public class NewThreadFirstQueue<R extends Runnable> extends LinkedBlockingQueue<R> {
    
    private ThreadPoolExecutor executor;
    
    public NewThreadFirstQueue(int capacity) {
        super(capacity);
    }
    
    @Override
    public boolean offer(@NotNull R runnable) {
        //have free worker. put task into queue to let the worker deal with task.
        if(executor.getActiveCount() < executor.getPoolSize()) {
            return super.offer(runnable);
        }
        synchronized(this) {
            //return false to let executor create new worker.
            if(executor.getPoolSize() < executor.getMaximumPoolSize()) {
                return false;
            }
        }
        //poolSize >= max
        return super.offer(runnable);
    }
}
