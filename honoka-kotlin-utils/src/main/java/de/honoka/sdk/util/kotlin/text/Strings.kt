package de.honoka.sdk.util.kotlin.text

fun String?.singleLine(whiteSpaceOnEnd: Boolean = false): String {
    val builder = StringBuilder()
    toString().trim().lineSequence().forEach {
        val line = it.trim()
        val startIndex = if(line.startsWith("|")) 1 else 0
        val endIndex = if(line.endsWith("|")) line.length - 1 else line.length
        if(startIndex >= endIndex) return@forEach
        builder.append(line.substring(startIndex, endIndex))
    }
    return builder.toString()
}