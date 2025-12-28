package de.honoka.sdk.util.kotlin.reflect

import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

fun KClass<*>.isSubclassOfAny(vararg classes: KClass<*>): Boolean = run {
    classes.firstOrNull { isSubclassOf(it) } != null
}
