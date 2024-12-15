package de.honoka.sdk.util.kotlin.code

import java.util.concurrent.RejectedExecutionHandler
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy

object ThreadPoolUtils {
    
    fun newScheduledPool(
        coreSize: Int, rejectedExecutionHandler: RejectedExecutionHandler = AbortPolicy()
    ): ScheduledThreadPoolExecutor = run {
        ScheduledThreadPoolExecutor(coreSize, rejectedExecutionHandler).apply {
            /*
             * 任务取消时将定时任务的待执行单元从队列中删除，默认是false。在默认情况下，如果直接取消任务，
             * 并不会从队列中删除此任务的待执行单元。
             *
             * 譬如，一个定时任务被设置为每5秒触发一次，则该任务在每次开始执行时，都会向队列中添加一个该
             * 任务的待执行单元，并在5秒后自动开始执行。
             */
            removeOnCancelPolicy = true
            //shutdown被调用后是否还执行队列中的延迟任务
            executeExistingDelayedTasksAfterShutdownPolicy = false
            //shutdown被调用后是否继续执行正在执行的任务
            continueExistingPeriodicTasksAfterShutdownPolicy = false
        }
    }
}
