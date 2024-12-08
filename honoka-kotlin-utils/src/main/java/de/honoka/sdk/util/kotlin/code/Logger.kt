package de.honoka.sdk.util.kotlin.code

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

internal object LoggerCache {
    
    val cache: MutableMap<KClass<*>, Logger> = ConcurrentHashMap()
}

val <T : Any> KClass<T>.log: Logger
    get() = LoggerCache.cache[this] ?: LoggerFactory.getLogger(java).also {
        LoggerCache.cache[this] = it
    }

val Any.log: Logger
    get() = this::class.log