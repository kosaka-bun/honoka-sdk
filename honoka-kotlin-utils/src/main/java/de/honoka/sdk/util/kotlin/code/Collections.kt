package de.honoka.sdk.util.kotlin.code

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
