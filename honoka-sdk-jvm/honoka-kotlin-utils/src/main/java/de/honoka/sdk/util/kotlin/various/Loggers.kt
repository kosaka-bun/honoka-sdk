package de.honoka.sdk.util.kotlin.various

import ch.qos.logback.classic.Level
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

private val loggerCache = ConcurrentHashMap<KClass<*>, Logger>()

val KClass<*>.log: Logger
    get() {
        loggerCache[this]?.let { return it }
        var clazz = java
        if(clazz.simpleName.lowercase().contains($$$"$$springcglib")) {
            clazz = java.superclass ?: clazz
        }
        val logger = LoggerFactory.getLogger(clazz).also {
            loggerCache[this] = it
        }
        return logger
    }

val Any.log: Logger
    get() = this::class.log

fun Logger.off() {
    when(this) {
        is ch.qos.logback.classic.Logger -> level = Level.OFF
        else -> throw UnsupportedOperationException()
    }
}
