package de.honoka.sdk.util.kotlin.code

import kotlin.reflect.KClass

fun <T : Any, U : Any> KClass<T>.isSubClassOf(clazz: KClass<U>): Boolean = clazz.java.isAssignableFrom(java)

@JvmName("tryBlockNullable")
inline fun <T> tryBlock(
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
            exceptionTypesToIgnore.filter { t::class.isSubClassOf(it) }.ifEmpty { throw t }
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
): T = tryBlock<T?>(times, throwOnExceedTimes, exceptionTypesToIgnore, block)!!

inline fun repeatRunCatching(times: Int, block: (Int) -> Unit) {
    repeat(times) {
        runCatching { block(times) }
    }
}
