package de.honoka.sdk.util.kotlin.code

import kotlin.reflect.KClass

fun exception(message: String? = null): Nothing = throw RuntimeException(message)

fun <T : Throwable> Throwable?.isAnyType(vararg types: KClass<T>): Boolean {
    this ?: return false
    return this::class.isSubClassOfAny(*types)
}
