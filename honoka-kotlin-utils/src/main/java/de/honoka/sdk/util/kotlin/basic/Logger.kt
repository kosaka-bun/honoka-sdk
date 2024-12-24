package de.honoka.sdk.util.kotlin.basic

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

interface HasLogger

private object LoggerCache {
    
    val cache: MutableMap<KClass<*>, Logger> = ConcurrentHashMap()
}

private val KClass<*>.log: Logger
    get() = LoggerCache.cache[this] ?: run {
        var clazz = java
        if(clazz.simpleName.lowercase().contains("\$\$springcglib")) {
            java.superclass?.let { clazz = it }
        }
        LoggerFactory.getLogger(clazz).also {
            LoggerCache.cache[this] = it
        }
    }

val HasLogger.log: Logger
    get() = this::class.log
