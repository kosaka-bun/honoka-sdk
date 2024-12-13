package de.honoka.sdk.util.kotlin.code

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

internal object LoggerCache {
    
    val cache: MutableMap<KClass<*>, Logger> = ConcurrentHashMap()
}

val KClass<*>.log: Logger
    get() {
        LoggerCache.cache[this]?.let { return it }
        var clazz = java
        if(clazz.simpleName.lowercase().contains("\$\$springcglib")) {
            java.superclass?.let { clazz = it }
        }
        val log = LoggerFactory.getLogger(clazz)
        LoggerCache.cache[this] = log
        return log
    }

val Any.log: Logger
    get() = this::class.log