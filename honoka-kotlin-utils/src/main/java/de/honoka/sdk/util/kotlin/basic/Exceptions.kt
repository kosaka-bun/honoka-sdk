package de.honoka.sdk.util.kotlin.basic

import kotlin.reflect.KClass

fun exception(message: String? = null): Nothing = throw RuntimeException(message)

fun <T : Throwable> Throwable?.isAny(vararg types: KClass<out T>): Boolean {
    this ?: return false
    return this::class.isSubclassOfAny(*types)
}

fun <T : Throwable> Throwable?.isAny(types: Collection<KClass<out T>>): Boolean = run {
    isAny(*types.toTypedArray())
}
