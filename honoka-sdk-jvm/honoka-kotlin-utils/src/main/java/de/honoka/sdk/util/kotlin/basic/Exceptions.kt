package de.honoka.sdk.util.kotlin.basic

import de.honoka.sdk.util.kotlin.reflect.isSubclassOfAny
import kotlin.reflect.KClass

class RemoteInvokeException(

    override val message: String,

    val stackTraceText: String
) : RuntimeException(message)

fun <T : Throwable> Throwable?.isAny(vararg types: KClass<out T>): Boolean {
    this ?: return false
    return this::class.isSubclassOfAny(*types)
}

fun <T : Throwable> Throwable?.isAny(types: Collection<KClass<out T>>): Boolean = run {
    isAny(*types.toTypedArray())
}

fun error(message: Any, cause: Throwable): Nothing {
    throw IllegalStateException(message.toString(), cause)
}
