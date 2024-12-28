package de.honoka.sdk.util.kotlin.basic

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

private val loggerCache = ConcurrentHashMap<KClass<*>, Logger>()

private val KClass<*>.log: Logger
    get() = loggerCache[this] ?: run {
        var clazz = java
        if(clazz.simpleName.lowercase().contains("\$\$springcglib")) {
            clazz = java.superclass ?: clazz
        }
        LoggerFactory.getLogger(clazz).also {
            loggerCache[this] = it
        }
    }

val Any.log: Logger
    get() = this::class.log
