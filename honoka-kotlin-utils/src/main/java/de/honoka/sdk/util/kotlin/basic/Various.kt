package de.honoka.sdk.util.kotlin.basic

import kotlin.reflect.KClass

fun <T : Any> KClass<*>.isSubClassOf(clazz: KClass<T>): Boolean = clazz.java.isAssignableFrom(java)

fun <T : Any> KClass<*>.isSubClassOfAny(vararg classes: KClass<T>): Boolean {
    classes.forEach {
        if(this.isSubClassOf(it)) return true
    }
    return false
}

@Suppress("UNCHECKED_CAST")
fun <T> Any?.cast(): T = this as T

inline fun <T> tryBlockNullable(
    times: Int,
    throwOnExceedTimes: Boolean = true,
    exceptionTypesToIgnore: List<KClass<out Throwable>> = listOf(Throwable::class),
    block: (Int) -> T?
): T? {
    var throwable: Throwable? = null
    repeat(times) { i ->
        try {
            return block(i)
        } catch(t: Throwable) {
            //若不是应当忽略的异常类型
            exceptionTypesToIgnore.filter {
                t::class.isSubClassOf(it)
            }.ifEmpty {
                throw t
            }
            throwable = t
        }
    }
    if(throwOnExceedTimes) throw throwable!!
    return null
}

inline fun <T : Any> tryBlock(
    times: Int,
    throwOnExceedTimes: Boolean = true,
    exceptionTypesToIgnore: List<KClass<out Throwable>> = listOf(Throwable::class),
    block: (Int) -> T
): T = tryBlockNullable(times, throwOnExceedTimes, exceptionTypesToIgnore, block)!!

inline fun repeatCatching(times: Int, block: (Int) -> Unit) {
    repeat(times) {
        runCatching { block(times) }
    }
}
