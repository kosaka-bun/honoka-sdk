package de.honoka.sdk.util.kotlin.code

import kotlin.reflect.KClass

fun <T : Any, U : Any> KClass<T>.isSubClassOf(clazz: KClass<U>): Boolean = clazz.java.isAssignableFrom(java)

inline fun <T> tryBlock(
    times: Int,
    exceptionTypesToIgnore: List<KClass<out Throwable>> = listOf(Throwable::class),
    block: () -> T
): T {
    var throwable: Throwable? = null
    repeat(times) {
        try {
            return block()
        } catch(t: Throwable) {
            //若不是应当忽略的异常类型
            exceptionTypesToIgnore.filter { t::class.isSubClassOf(it) }.ifEmpty { throw t }
            throwable = t
        }
    }
    throw throwable!!
}
