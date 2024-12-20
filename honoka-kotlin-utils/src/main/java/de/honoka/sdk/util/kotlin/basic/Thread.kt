package de.honoka.sdk.util.kotlin.basic

import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

/**
 * 将对象存入ThreadLocal中，然后执行一段代码块，执行完成后清除ThreadLocal中的对象，
 * 最后返回代码块的返回值。
 * 用于在线程池中的线程使用ThreadLocal时，防止未正确释放ThreadLocal中的内容。
 */
inline fun <T> ThreadLocal<T>.use(t: T, block: () -> Any?): Any? {
    try {
        set(t)
        return block()
    } finally {
        remove()
    }
}

fun <T> Future<T>.getOrCancel(timeout: Long, unit: TimeUnit): T {
    try {
        return get(timeout, unit)
    } catch(t: Throwable) {
        cancel(true)
        throw t
    }
}

fun ExecutorService.shutdownNowAndWait(
    timeout: Long = Long.MAX_VALUE,
    unit: TimeUnit = TimeUnit.SECONDS
): List<Runnable> = shutdownNow().apply { awaitTermination(timeout, unit) }
