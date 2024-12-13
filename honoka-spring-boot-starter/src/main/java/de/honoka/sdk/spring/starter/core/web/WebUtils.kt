package de.honoka.sdk.spring.starter.core.web

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders

/**
 * Web相关工具
 */
object WebUtils {

    val HttpServletRequest.clientRealIp: String?
        get() = getRealIp( this)
    
    val HttpServletRequest.authorization: List<String?>
        get() {
            getHeader(HttpHeaders.AUTHORIZATION)?.split(" ")?.run {
                return if(size < 2) listOf(null, first()) else this
            }
            return listOf(null, null)
        }

    /**
     * 从请求中获取请求方的真实IP，忽略反向代理
     */
    private fun getRealIp(request: HttpServletRequest): String? {
        //反向代理后，转发请求的HTTP头信息中，增加了X-Real-IP等信息
        val headerNames = listOf("X-Real-IP", "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP")
        var ip: String? = null
        for(it in headerNames) {
            ip = request.getHeader(it)
            if(ip?.isNotBlank() == true && !ip.equals("unknown", true)) break
            ip = null
        }
        ip = ip ?: request.remoteAddr ?: return null
        if(ip.contains(",")) {
            ip = ip.split(",")[0]
        }
        if(ip in listOf("::1", "0:0:0:0:0:0:0:1")) {
            return "127.0.0.1"
        }
        return ip
    }
    
    operator fun Array<Cookie>?.get(name: String): String? = this?.firstOrNull { it.name == name }?.value
    
    fun cookieStringToMap(cookieString: String): Map<String, String> {
        val map = HashMap<String, String>()
        cookieString.split("; ").forEach {
            val separateIndex = it.indexOf("=")
            if(separateIndex < 0) return@forEach
            val key = it.substring(0, separateIndex)
            val value = if(separateIndex < it.length - 1) {
                it.substring(separateIndex + 1)
            } else ""
            map[key] = value
        }
        return map
    }
}
