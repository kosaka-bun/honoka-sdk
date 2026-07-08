package de.honoka.sdk.util.kotlin.various

private val AS_IS_CONVERTER: (String?) -> String? = { it }

@Suppress("UNCHECKED_CAST")
fun <T : String?> readlnOrDefault(msg: String, default: T? = null): T =
    readlnOrDefault(msg, default, AS_IS_CONVERTER) as T

@Suppress("UNCHECKED_CAST")
fun <T> readlnOrDefault(msg: String, default: T? = null, converter: (String) -> T): T {
    val msgWithDefault = msg + (default?.let { "（默认为${default}）" } ?: "")
    print("请输入$msgWithDefault：")
    while(true) {
        val read = readln()
        if(default is CharSequence) {
            return read.ifEmpty { default } as T
        }
        val result = runCatching {
            (if(read.isBlank()) default else converter(read.trim())) as T
        }.getOrElse {
            print("输入错误，请重新输入$msgWithDefault：")
            continue
        }
        println("$msg：$result")
        return result
    }
}
