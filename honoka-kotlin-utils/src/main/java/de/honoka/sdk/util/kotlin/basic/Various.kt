package de.honoka.sdk.util.kotlin.basic

import de.honoka.sdk.util.basic.CodeUtils
import org.slf4j.event.Level
import java.util.*
import kotlin.reflect.KClass

fun <T : Any> KClass<*>.isSubClassOf(clazz: KClass<T>): Boolean = clazz.java.isAssignableFrom(java)

fun <T : Any> KClass<*>.isSubClassOfAny(vararg classes: KClass<out T>): Boolean {
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

val Date.weekdayNum: Int
    get() = Calendar.getInstance().run {
        setTime(this@weekdayNum)
        get(Calendar.DAY_OF_WEEK).let {
            if(it != Calendar.SUNDAY) it - 1 else 7
        }
    }

fun <T> Result<T>.printStackIfFailed() {
    if(isSuccess) return
    exceptionOrNull()?.printStackTrace()
}

fun <T> Result<T>.logIfFailed(level: Level = Level.ERROR, msg: String = "") {
    if(isSuccess) return
    val log = CodeUtils.getCallerClass().kotlin.log
    val throwable = exceptionOrNull() ?: return
    when(level) {
        Level.ERROR -> log.error(msg, throwable)
        Level.INFO -> log.info(msg, throwable)
        Level.WARN -> log.warn(msg, throwable)
        Level.DEBUG -> log.debug(msg, throwable)
        Level.TRACE -> log.trace(msg, throwable)
        else -> log.error(msg, throwable)
    }
}
