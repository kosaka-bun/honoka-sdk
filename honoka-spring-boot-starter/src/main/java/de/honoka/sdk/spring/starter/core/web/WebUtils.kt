package de.honoka.sdk.spring.starter.core.web

/**
 * Web相关工具
 */
object WebUtils {
    
    fun cookieStringToMap(cookieString: String): Map<String, String> {
        val map = HashMap<String, String>()
        cookieString.split("; ").forEach {
            val separateIndex = it.indexOf("=")
            if(separateIndex < 0) return@forEach
            val key = it.substring(0, separateIndex)
            val value = if(separateIndex < it.length - 1) {
                it.substring(separateIndex + 1)
            } else {
                ""
            }
            map[key] = value
        }
        return map
    }
}
