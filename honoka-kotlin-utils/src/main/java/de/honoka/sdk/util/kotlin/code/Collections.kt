package de.honoka.sdk.util.kotlin.code

fun <K, V> MutableMap<K, V>.removeIf(block: (entry: Map.Entry<K, V>) -> Boolean) {
    iterator().run {
        while(hasNext()) {
            val entry = next()
            if(block(entry)) remove()
        }
    }
}