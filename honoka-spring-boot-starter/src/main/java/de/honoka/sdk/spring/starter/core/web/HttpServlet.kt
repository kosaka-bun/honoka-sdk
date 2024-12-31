package de.honoka.sdk.spring.starter.core.web

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

/**
 * 从请求中获取请求方的真实IP，忽略反向代理
 */
val HttpServletRequest.clientRealIp: String?
    get() {
        //反向代理后，转发请求的HTTP头信息中，增加了X-Real-IP等信息
        val headerNames = listOf("X-Real-IP", "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP")
        var ip: String? = null
        for(it in headerNames) {
            ip = getHeader(it)
            if(ip?.isNotBlank() == true && !ip.equals("unknown", true)) break
            ip = null
        }
        ip = ip ?: remoteAddr ?: return null
        if(ip.contains(",")) {
            ip = ip.split(",")[0]
        }
        if(ip in listOf("::1", "0:0:0:0:0:0:0:1")) {
            return "127.0.0.1"
        }
        return ip
    }

/**
 * 长度为2的`List`，第一个元素为Token类型（如Bearer），第二个元素为Token值
 */
val HttpServletRequest.authorization: List<String?>
    get() = run {
        getHeader(HttpHeaders.AUTHORIZATION)?.split(" ")?.run {
            if(size < 2) listOf(null, first()) else this
        } ?: listOf(null, null)
    }

operator fun Array<Cookie>?.get(name: String): String? = this?.firstOrNull { it.name == name }?.value

fun HttpServletRequest.canAcceptJson(): Boolean {
    val accept = getHeader(HttpHeaders.ACCEPT)
    if(accept.contains(MediaType.APPLICATION_JSON_VALUE)) {
        return true
    }
    if(accept.contains(MediaType.ALL_VALUE)) {
        return !accept.contains(MediaType.TEXT_HTML_VALUE)
    }
    return false
}
