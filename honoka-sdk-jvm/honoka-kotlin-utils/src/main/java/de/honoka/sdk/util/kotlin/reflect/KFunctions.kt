package de.honoka.sdk.util.kotlin.reflect

import kotlin.reflect.KCallable
import kotlin.reflect.full.callSuspend

fun <T> KCallable<T>.callAdaptive(vararg args: Any?): T = run {
    call(*toAdaptiveArgs(this, args))
}

suspend fun <T> KCallable<T>.callSuspendAdaptive(vararg args: Any?): T = run {
    callSuspend(*toAdaptiveArgs(this, args))
}

private fun toAdaptiveArgs(callable: KCallable<*>, args: Array<*>): Array<Any?> {
    val realArgs = args.take(callable.parameters.size)
    if(realArgs.size != callable.parameters.size) {
        error("The args size is not equal with parameters size.")
    }
    return realArgs.toTypedArray()
}
