package de.honoka.sdk.util.kotlin.basic

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

inline fun <K, V> Map<K, V>.forEachKv(action: (K, V) -> Unit) {
    entries.forEach {
        action(it.key, it.value)
    }
}

inline fun <T> Iterable<T>.forEachCatching(action: (T) -> Unit) {
    forEach {
        runCatching {
            action(it)
        }
    }
}

inline fun <K, V> Map<K, V>.forEachCatching(action: (K, V) -> Unit) {
    forEachKv { k, v ->
        runCatching {
            action(k, v)
        }
    }
}
