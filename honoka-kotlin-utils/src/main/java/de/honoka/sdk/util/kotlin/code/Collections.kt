package de.honoka.sdk.util.kotlin.code

inline fun <T> Iterable<T>.iterate(block: (T, Iterator<T>) -> Unit) {
    iterator().run {
        while(hasNext()) {
            block(next(), this)
        }
    }
}

inline fun <K, V> MutableMap<K, V>.removeIf(block: (Map.Entry<K, V>) -> Boolean) {
    iterator().run {
        while(hasNext()) {
            val entry = next()
            if(block(entry)) remove()
        }
    }
}
