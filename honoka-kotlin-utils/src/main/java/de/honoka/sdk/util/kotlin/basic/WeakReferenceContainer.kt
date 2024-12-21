package de.honoka.sdk.util.kotlin.basic

import de.honoka.sdk.util.basic.javadoc.ThreadSafe
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KProperty

/**
 * 采用对象的弱引用作为key的对象属性值容器，用于支持在interface中直接定义可赋值的属性
 */
@ThreadSafe
@Suppress("UNCHECKED_CAST", "MemberVisibilityCanBePrivate")
object WeakReferenceContainer {
    
    private val map = ConcurrentHashMap<WeakReference<Any>, PropertyValueMap>()
    
    private val nullValue = Any()
    
    private val Any.propertyValueMap: PropertyValueMap?
        get() = map.entries.firstOrNull { it.key.get() == this }?.value
    
    private val Any.propertyValueMapOrNew: PropertyValueMap
        get() = propertyValueMap ?: synchronized(this) {
            propertyValueMap ?: PropertyValueMap().also {
                map[WeakReference(this)] = it
            }
        }
    
    fun <T> get(obj: Any, property: KProperty<*>): T? {
        clean()
        val result = obj.propertyValueMap?.get(property)
        return if(result === nullValue) null else result as T?
    }
    
    fun <T> getOrInit(obj: Any, property: KProperty<*>, initialValue: T): T = run {
        getOrInitNullable(obj, property, initialValue)!!
    }
    
    fun <T> getOrInitNullable(obj: Any, property: KProperty<*>, initialValue: T?): T? {
        clean()
        obj.propertyValueMap?.get(property)?.let {
            return if(it === nullValue) null else it as T
        }
        synchronized(obj) {
            obj.propertyValueMap?.get(property)?.let {
                return if(it === nullValue) null else it as T
            }
            obj.propertyValueMapOrNew[property] = initialValue ?: nullValue
            return initialValue
        }
    }
    
    fun set(obj: Any, property: KProperty<*>, value: Any?) {
        getOrInitNullable<Any?>(obj, property, null)
        obj.propertyValueMap?.put(property, value ?: nullValue)
        clean()
    }
    
    private fun clean() {
        map.forEach { (k) ->
            k.get() ?: map.remove(k)
        }
    }
}

private typealias PropertyValueMap = ConcurrentHashMap<KProperty<*>, Any>
