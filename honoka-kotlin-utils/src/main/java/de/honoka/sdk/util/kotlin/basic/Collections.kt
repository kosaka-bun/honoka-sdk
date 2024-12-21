package de.honoka.sdk.util.kotlin.basic

import java.util.concurrent.ConcurrentMap

data class MapEntryImpl<K, V>(override val key: K, override val value: V) : Map.Entry<K, V>

inline fun <T> MutableIterable<T>.iterate(block: MutableIterator<T>.(T) -> Unit) {
    iterator().run {
        while(hasNext()) {
            block(next())
        }
    }
}

inline fun <K, V> MutableMap<K, V>.removeIf(block: (K, V) -> Boolean) {
    iterator().run {
        while(hasNext()) {
            val entry = next()
            if(block(entry.key, entry.value)) remove()
        }
    }
}

inline fun <T> Iterable<T>.forEachRun(action: T.() -> Unit) {
    forEach {
        it.action()
    }
}

inline fun <T> Iterable<T>.forEachCatching(action: (T) -> Unit) {
    forEach {
        runCatching {
            action(it)
        }
    }
}

inline fun <K, V> Map<K, V>.forEachCatching(action: (Map.Entry<K, V>) -> Unit) {
    forEach {
        runCatching {
            action(it)
        }
    }
}

inline fun <T> Iterable<T>.forEachCatchingRun(action: T.() -> Unit) {
    forEachCatching {
        it.action()
    }
}

/*
 * ConcurrentMap的forEach比较特殊，它可以在遍历过程中即时发现被修改或移除的键值对。
 * 若某个键值对在forEach遍历到它之前就被移除了，此方法在后续的遍历中不会再遍历它。
 *
 * IDEA并未对ConcurrentMap的特殊性进行处理，尝试调用ConcurrentMap的原生forEach方法
 * 时会被建议替换为Kotlin的forEach扩展函数。
 */
@Suppress("JavaMapForEach")
inline fun <K, V> ConcurrentMap<K, V>.forEachInstant(crossinline action: (Map.Entry<K, V>) -> Unit) {
    forEach { k, v ->
        action(MapEntryImpl(k, v))
    }
}
