@file:Suppress("UNCHECKED_CAST")

package de.honoka.sdk.util.kotlin.reflect

import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.allSuperclasses
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.isAccessible

fun KClass<*>.isSubclassOfAny(vararg classes: KClass<*>): Boolean =
    classes.firstOrNull { isSubclassOf(it) } != null

fun <T : Any> KClass<T>.setInstanceProp(receiver: T, name: String, value: Any?) {
    val prop = declaredMemberProperties.firstOrNull {
        it.name == name
    } ?: allSuperclasses.firstNotNullOf { c ->
        c.declaredMemberProperties.firstOrNull { it.name == name }
    }
    prop as KMutableProperty1<T, Any?>
    prop.run {
        isAccessible = true
        set(receiver, value)
    }
}
