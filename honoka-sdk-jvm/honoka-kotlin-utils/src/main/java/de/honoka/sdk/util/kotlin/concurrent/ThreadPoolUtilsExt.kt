package de.honoka.sdk.util.kotlin.concurrent

import de.honoka.sdk.util.concurrent.ThreadPoolUtils
import java.util.concurrent.RejectedExecutionHandler
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy

object ThreadPoolUtilsExt {
    
    fun newScheduledPool(
        coreSize: Int, rejectedExecutionHandler: RejectedExecutionHandler = AbortPolicy()
    ): ScheduledThreadPoolExecutor = run {
        ThreadPoolUtils.newScheduledPool(coreSize, rejectedExecutionHandler)
    }
}
