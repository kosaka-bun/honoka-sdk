@file:Suppress("UNCHECKED_CAST")

package de.honoka.sdk.util.kotlin.reflect

import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.memberProperties

operator fun Annotation.get(key: String): Any {
    val prop = annotationClass.memberProperties.run {
        first { it.name == key } as KProperty1<Annotation, Any>
    }
    return prop.get(this)
}

fun Annotation.getString(key: String): String = this[key] as String

fun Annotation.getInt(key: String): Int = this[key] as Int

fun Annotation.getBoolean(key: String): Boolean = this[key] as Boolean

fun KAnnotatedElement.findAnyAnnotation(vararg classes: KClass<out Annotation>): Annotation? = run {
    classes.firstNotNullOfOrNull { findAnnotations(it).firstOrNull() }
}
