package de.honoka.sdk.util.kotlin.reflect

import kotlin.reflect.KCallable
import kotlin.reflect.full.callSuspend

fun <T> KCallable<T>.callAdaptive(receiver: Any?, vararg args: Any?): T = run {
    val realArgs = toAdaptiveArgs(this, args)
    if(receiver != null) {
        call(receiver, realArgs)
    } else {
        call(realArgs)
    }
}

suspend fun <T> KCallable<T>.callSuspendAdaptive(receiver: Any?, vararg args: Any?): T = run {
    val realArgs = toAdaptiveArgs(this, args)
    if(receiver != null) {
        callSuspend(receiver, realArgs)
    } else {
        callSuspend(realArgs)
    }
}

private fun toAdaptiveArgs(callable: KCallable<*>, vararg args: Any?): Array<Any?> {
    val realArgs = args.take(callable.parameters.size)
    if(realArgs.size != callable.parameters.size) {
        error("The args size is not equal with parameters size.")
    }
    return realArgs.toTypedArray()
}
