package de.honoka.sdk.util.kotlin.basic

import de.honoka.sdk.util.basic.javadoc.ThreadSafe
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.ThreadPoolExecutor.DiscardPolicy
import java.util.concurrent.TimeUnit
import kotlin.jvm.internal.CallableReference
import kotlin.reflect.KProperty

/**
 * 采用对象的弱引用作为key的对象属性值容器，用于支持在interface中直接定义可赋初始值的属性。
 *
 * 注意：对象的属性在使用本类存取属性值时会有一定性能问题，速度在理论上远不如直接使用对象中的字段来
 * 进行属性值存取的属性。
 */
@ThreadSafe
@Suppress("UNCHECKED_CAST", "MemberVisibilityCanBePrivate")
object PropertyValueContainer {
    
    private class KPropertyReference(property: KProperty<*>) {
        
        val ref = WeakReference(property.cast<CallableReference>().boundReceiver)
        
        val signature: String = property.toString()
        
        val targetHashCode: Int = Objects.hash(ref.get(), signature)
        
        override fun equals(other: Any?): Boolean {
            if(other !is KPropertyReference) return false
            val target = ref.get() ?: return this === other
            val result = BooleanBuilder.calc {
                init(target === other.ref.get())
                and(signature == other.signature)
                and(targetHashCode == other.targetHashCode)
            }
            return result
        }
        
        override fun hashCode(): Int = targetHashCode
    }
    
    private val map = ConcurrentHashMap<KPropertyReference, Any>()
    
    private val nullValue = Any()
    
    private val executor by lazy {
        ThreadPoolExecutor(
            1, 1, 0, TimeUnit.MILLISECONDS,
            executorQueue, DiscardPolicy()
        )
    }
    
    private val executorQueue = LinkedBlockingQueue<Runnable>(1)
    
    @Volatile
    private var lastCleanTime = 0L
    
    fun <T : Any> get(property: KProperty<*>): T = getOrNull(property)!!
    
    fun <T> getOrNull(property: KProperty<*>): T? {
        val result = map[KPropertyReference(property)]
        clean()
        return if(result === nullValue) null else result as T?
    }
    
    fun <T : Any> getOrInit(property: KProperty<*>, initialValue: T): T = run {
        getOrInit(property, initialValue as T?)!!
    }
    
    @JvmName("getOrInitNullable")
    fun <T> getOrInit(property: KProperty<*>, initialValue: T?): T? {
        try {
            property as CallableReference
            val ref = KPropertyReference(property)
            map[ref]?.let {
                return if(it === nullValue) null else it as T
            }
            synchronized(property.boundReceiver) {
                map[ref]?.let {
                    return if(it === nullValue) null else it as T
                }
                map[ref] = initialValue ?: nullValue
                return initialValue
            }
        } finally {
            clean()
        }
    }
    
    fun set(property: KProperty<*>, value: Any?) {
        map[KPropertyReference(property)] = value ?: nullValue
        clean()
    }
    
    private fun clean() {
        val time = System.currentTimeMillis()
        if(time - lastCleanTime < 1000) return
        if(executorQueue.isNotEmpty()) return
        lastCleanTime = time
        executor.submit {
            map.removeIf { (k) -> k.ref.get() == null }
        }
    }
}
