package de.honoka.sdk.util.kotlin.lang

import de.honoka.sdk.util.kotlin.reflect.isSubclassOfAny
import kotlin.reflect.KClass

fun <T : Throwable> Throwable?.isAny(vararg types: KClass<out T>): Boolean =
    this?.let { it::class.isSubclassOfAny(*types) } ?: false

fun <T : Throwable> Throwable?.isAny(types: Collection<KClass<out T>>): Boolean =
    isAny(*types.toTypedArray())

fun error(message: Any, cause: Throwable): Nothing {
    throw IllegalStateException(message.toString(), cause)
}
