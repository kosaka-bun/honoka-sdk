package de.honoka.sdk.util.kotlin.various

import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST")
class DirectDelegate<T>(private val property: KProperty<*>) {

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T =
        this.property.getter.call() as T

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.property.cast<KMutableProperty<*>>().setter.call(value)
    }
}
