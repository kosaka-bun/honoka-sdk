package de.honoka.sdk.util.kotlin.text

class StringWrapper(internal var str: StringBuilder) {
    
    fun append(vararg s: Any?) {
        str.append(*s)
    }
    
    fun replace(oldValue: String, newValue: String, ignoreCase: Boolean = false) {
        str = str.toString().replace(oldValue, newValue, ignoreCase).toStringBuilder()
    }
    
    fun replace(regex: Regex, replacement: String) {
        str = str.replace(regex, replacement).toStringBuilder()
    }
    
    fun trim() {
        str = str.trim().toString().toStringBuilder()
    }
}

fun String?.process(block: StringWrapper.() -> Unit): String {
    val wrapper = StringWrapper(this?.toStringBuilder() ?: StringBuilder())
    wrapper.block()
    return wrapper.str.toString()
}
