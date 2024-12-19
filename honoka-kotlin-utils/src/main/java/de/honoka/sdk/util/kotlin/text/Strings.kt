package de.honoka.sdk.util.kotlin.text

/**
 * 将多行字符串拼接为单行，支持行边界字符，默认使用“|”字符作为行边界。
 *
 * 基本逻辑：
 * 1. 去掉传入字符串两端的空格。
 * 2. 将字符串拆分为行。
 * 3. 遍历每行，先去掉每行两端的空格。
 * 4. 若某行以“|”开头，则去掉第一个“|”。以“|”结尾，则去掉最后一个“|”。
 * 5. 将处理好的行拼接到builder中。
 * 6. 返回builder的值。
 *
 * 注意事项：
 * 1. 本方法仅适合用于Kotlin代码中的多行文本字面量。
 * 2. 若某行以`${}`开头或结尾，由于不知道表达式最终的值是否会以“|”开头或结尾，为了避免去除
 *    表达式结果当中包含的“|”，请在以`${}`开头或结尾的行的行首或行尾额外添加一个“|”。
 */
fun String?.singleLine(borderChar: Char = '|', whiteSpaceOnEnd: Boolean = false): String {
    val builder = StringBuilder()
    toString().trim().lineSequence().forEach {
        val line = it.trim()
        val startIndex = if(line.startsWith(borderChar)) 1 else 0
        val endIndex = if(line.endsWith(borderChar)) line.lastIndex else line.length
        if(startIndex >= endIndex) return@forEach
        builder.run {
            append(line.substring(startIndex, endIndex))
            if(whiteSpaceOnEnd) append(" ")
        }
    }
    return builder.toString().trim()
}

/**
 * 直接将多行字符串的每行去除两端空格后，拼接在一起。
 */
fun String?.simpleSingleLine(whiteSpaceOnEnd: Boolean = false): String {
    val builder = StringBuilder()
    toString().trim().lineSequence().forEach {
        builder.run {
            append(it.trim())
            if(whiteSpaceOnEnd) append(" ")
        }
    }
    return builder.toString()
}

fun String.toStringBuilder(): StringBuilder = StringBuilder(this)
