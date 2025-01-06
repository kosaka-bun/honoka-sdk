package de.honoka.sdk.util.concurrent;

import java.util.concurrent.*;

public class ThreadPoolUtils {
    
    private static final RejectedExecutionHandler defaultRejectedExecutionHandler =
        new ThreadPoolExecutor.AbortPolicy();
    
    public static ScheduledThreadPoolExecutor newScheduledPool(
        int coreSize, RejectedExecutionHandler rejectedExecutionHandler
    ) {
        if(rejectedExecutionHandler == null) {
            rejectedExecutionHandler = defaultRejectedExecutionHandler;
        }
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(coreSize, rejectedExecutionHandler);
        /*
         * 任务取消时将定时任务的待执行单元从队列中删除，默认是false。在默认情况下，如果直接取消任务，
         * 并不会从队列中删除此任务的待执行单元。
         */
        executor.setRemoveOnCancelPolicy(true);
        //shutdown被调用后是否还执行队列中的延迟任务
        executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        //shutdown被调用后是否继续执行正在执行的任务
        executor.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
        return executor;
    }
    
    /**
     * 使用{@link NewThreadFirstQueue}创建线程池
     */
    public static ThreadPoolExecutor newEagerThreadPool(
        int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit
    ) {
        return newEagerThreadPool(
            corePoolSize, maximumPoolSize, keepAliveTime, unit, Integer.MAX_VALUE
        );
    }
    
    public static ThreadPoolExecutor newEagerThreadPool(
        int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
        int queueCapacity
    ) {
        return newEagerThreadPool(
            corePoolSize, maximumPoolSize, keepAliveTime, unit, queueCapacity,
            Executors.defaultThreadFactory(), defaultRejectedExecutionHandler
        );
    }
    
    public static ThreadPoolExecutor newEagerThreadPool(
        int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
        int queueCapacity, ThreadFactory threadFactory
    ) {
        return newEagerThreadPool(
            corePoolSize, maximumPoolSize, keepAliveTime, unit, queueCapacity,
            threadFactory, defaultRejectedExecutionHandler
        );
    }
    
    public static ThreadPoolExecutor newEagerThreadPool(
        int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
        int queueCapacity, RejectedExecutionHandler handler
    ) {
        return newEagerThreadPool(
            corePoolSize, maximumPoolSize, keepAliveTime, unit, queueCapacity,
            Executors.defaultThreadFactory(), handler
        );
    }
    
    public static ThreadPoolExecutor newEagerThreadPool(
        int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
        int queueCapacity, ThreadFactory threadFactory, RejectedExecutionHandler handler
    ) {
        NewThreadFirstQueue<Runnable> queue = new NewThreadFirstQueue<>(queueCapacity);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            corePoolSize, maximumPoolSize, keepAliveTime, unit, queue,
            threadFactory, handler
        );
        queue.setExecutor(executor);
        return executor;
    }
}
