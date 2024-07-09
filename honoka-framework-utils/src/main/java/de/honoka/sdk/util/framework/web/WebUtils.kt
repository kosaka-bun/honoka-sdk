package de.honoka.sdk.util.framework.web

import javax.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequest as JakartaHttpServletRequest

/**
 * Web相关工具
 */
object WebUtils {

    val HttpServletRequest.clientRealIp: String?
        get() = getRealIp(this, null)

    val JakartaHttpServletRequest.clientRealIp: String?
        get() = getRealIp(null, this)

    /**
     * 从请求中获取请求方的真实IP，忽略反向代理
     */
    private fun getRealIp(request0: HttpServletRequest?, request1: JakartaHttpServletRequest?): String? {
        //反向代理后，转发请求的HTTP头信息中，增加了X-Real-IP等信息
        val headerNames = listOf("X-Real-IP", "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP")
        var ip: String? = null
        for(it in headerNames) {
            ip = request0?.getHeader(it) ?: request1?.getHeader(it)
            if(ip?.isNotBlank() == true && !ip.equals("unknown", true)) break
            ip = null
        }
        ip = ip ?: request0?.remoteAddr ?: request1?.remoteAddr ?: return null
        if(ip.contains(",")) {
            ip = ip.split(",")[0]
        }
        if(ip in listOf("::1", "0:0:0:0:0:0:0:1")) {
            return "127.0.0.1"
        }
        return ip
    }
}
