package de.honoka.sdk.util.kotlin.basic

import kotlin.reflect.KClass

/*
 * 注意：使用该类进行逻辑运算时，会影响逻辑运算的短路规则，仅适用于对短路规则没有硬性要求的运算。
 */
class BooleanBuilder(private var boolean: Boolean = false) {
    
    companion object {
        
        inline fun calc(block: BooleanBuilder.() -> Boolean): Boolean = BooleanBuilder().run(block)
    }
    
    private fun Boolean.save(): Boolean = also {
        boolean = it
    }
    
    fun init(value: Boolean): Boolean = value.save()
    
    fun and(value: Boolean): Boolean = (boolean && value).save()
    
    fun or(value: Boolean): Boolean = (boolean || value).save()
}

fun <T : Any> KClass<*>.isSubClassOf(clazz: KClass<T>): Boolean = clazz.java.isAssignableFrom(java)

fun <T : Any> KClass<*>.isSubClassOfAny(vararg classes: KClass<T>): Boolean {
    classes.forEach {
        if(this.isSubClassOf(it)) return true
    }
    return false
}

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <T> Any?.cast(): T = this as T

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
