package de.honoka.sdk.util.kotlin.reflect

import kotlin.reflect.KCallable
import kotlin.reflect.full.callSuspend

fun <T> KCallable<T>.callAdaptive(vararg args: Any?): T =
    call(*args.adaptWith(this))

suspend fun <T> KCallable<T>.callSuspendAdaptive(vararg args: Any?): T =
    callSuspend(*args.adaptWith(this))

private fun Array<*>.adaptWith(callable: KCallable<*>): Array<Any?> {
    val realArgs = take(callable.parameters.size)
    if(realArgs.size != callable.parameters.size) {
        error("The args size is not equal with parameters size.")
    }
    return realArgs.toTypedArray()
}
