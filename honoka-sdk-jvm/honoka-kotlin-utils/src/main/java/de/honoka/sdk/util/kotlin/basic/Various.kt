package de.honoka.sdk.util.kotlin.basic

import cn.hutool.json.JSON
import cn.hutool.json.JSONArray
import de.honoka.sdk.util.basic.CodeUtils
import org.slf4j.event.Level
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.starProjectedType

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <T> Any?.cast(): T = this as T

fun <T : Any> Any.tryCast(clazz: KClass<T>): T = tryCastOrNull(clazz)!!

fun <T : Any> Any.tryCast(type: KType): T = tryCastOrNull(type)!!

fun <T : Any> Any?.tryCastOrNull(clazz: KClass<T>): T? = tryCastOrNull(clazz.starProjectedType)

@Suppress("UNCHECKED_CAST")
fun <T> Any?.tryCastOrNull(type: KType): T? {
    this ?: return null
    when(this) {
        is JSON -> {
            val clazz = type.classifier as KClass<*>
            if(clazz.isSubclassOf(JSON::class)) {
                return this as T
            }
            if(clazz.isSubclassOf(Collection::class)) {
                val elementClass = type.arguments[0].type!!.classifier as KClass<*>
                val result = cast<JSONArray>().toList(elementClass.java).let {
                    if(clazz == Set::class) it.toSet() else it
                }
                return result as T
            }
            return toBean(clazz.java) as T
        }
        else -> return this as T
    }
}

inline fun <T : Any> tryBlock(
    times: Int,
    throwOnExceedTimes: Boolean = true,
    ignoredExceptionTypes: List<KClass<out Throwable>> = listOf(Throwable::class),
    block: (Int) -> T
): T = tryBlockOrNull(times, throwOnExceedTimes, ignoredExceptionTypes, block)!!

inline fun <T> tryBlockOrNull(
    times: Int,
    throwOnExceedTimes: Boolean = true,
    ignoredExceptionTypes: List<KClass<out Throwable>> = listOf(Throwable::class),
    block: (Int) -> T?
): T? {
    var throwable: Throwable? = null
    repeat(times) { i ->
        try {
            return block(i)
        } catch(t: Throwable) {
            //若不是应当忽略的异常类型
            ignoredExceptionTypes.firstOrNull {
                t::class.isSubclassOf(it)
            } ?: throw t
            throwable = t
        }
    }
    if(throwOnExceedTimes) throw throwable!!
    return null
}

inline fun repeatCatching(times: Int, block: (Int) -> Unit) {
    repeat(times) {
        runCatching {
            block(times)
        }
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
    }
}
