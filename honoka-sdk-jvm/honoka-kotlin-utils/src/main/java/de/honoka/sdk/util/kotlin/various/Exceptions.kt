package de.honoka.sdk.util.kotlin.various

import de.honoka.sdk.util.kotlin.reflect.isSubclassOfAny
import kotlin.reflect.KClass

class RemoteInvokeException(

    override val message: String,

    val stackTraceText: String
) : RuntimeException(message)

fun <T : Throwable> Throwable?.isAny(vararg types: KClass<out T>): Boolean =
    this?.let { it::class.isSubclassOfAny(*types) } ?: false

fun <T : Throwable> Throwable?.isAny(types: Collection<KClass<out T>>): Boolean =
    isAny(*types.toTypedArray())

fun error(message: Any? = null, cause: Throwable? = null): Nothing {
    throw IllegalStateException(message.toString(), cause)
}
