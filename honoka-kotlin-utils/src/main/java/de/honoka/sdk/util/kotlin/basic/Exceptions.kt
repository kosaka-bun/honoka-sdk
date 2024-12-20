package de.honoka.sdk.util.kotlin.basic

import kotlin.reflect.KClass

fun exception(message: String? = null): Nothing = throw RuntimeException(message)

fun <T : Throwable> Throwable?.isAnyType(vararg types: KClass<T>): Boolean {
    this ?: return false
    return this::class.isSubClassOfAny(*types)
}

fun <T : Throwable> Throwable?.isAnyType(types: Collection<KClass<T>>): Boolean = isAnyType(*types.toTypedArray())
