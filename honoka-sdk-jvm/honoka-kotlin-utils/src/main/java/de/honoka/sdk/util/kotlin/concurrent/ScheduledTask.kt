package de.honoka.sdk.util.kotlin.concurrent

import java.io.Closeable
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

class ScheduledTask(
    private val delay: String,
    private val initialDelay: String = "0s",
    private val action: () -> Unit
) : Closeable {
    
    private var executor: ScheduledThreadPoolExecutor? = null
    
    private var runningTask: ScheduledFuture<*>? = null
    
    var exceptionCallback: (Throwable) -> Unit = {}
    
    @Synchronized
    fun startup() {
        close()
        executor = ThreadPoolUtilsExt.newScheduledPool(1)
        val realAction: () -> Unit = {
            runCatching {
                action()
            }.getOrElse {
                exceptionCallback(it)
            }
        }
        val delay = Duration.parse(delay).inWholeMilliseconds
        val initialDelay = Duration.parse(initialDelay).inWholeMilliseconds
        runningTask = executor!!.scheduleWithFixedDelay(
            realAction, initialDelay, delay, TimeUnit.MILLISECONDS
        )
    }
    
    @Synchronized
    override fun close() {
        runningTask?.cancel(true)
        executor?.shutdownNowAndWait()
    }
}
