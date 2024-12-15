package de.honoka.sdk.util.kotlin.net.http

import cn.hutool.http.HttpUtil

@Suppress("MemberVisibilityCanBePrivate")
object HttpUtilExt {
    
    fun getWithBrowserHeaders(
        url: String,
        timeout: Int? = null,
        useApiHeaders: Boolean = false,
        useRandomProxy: Boolean = false
    ): String {
        val request = HttpUtil.createGet(url).apply {
            if(useApiHeaders) {
                browserApiHeaders()
            } else {
                browserHeaders()
            }
            timeout?.let { timeout(it) }
            if(useRandomProxy) randomProxy()
        }
        return request.execute().body()
    }
    
    fun getWithBrowserApiHeaders(
        url: String,
        timeout: Int? = null,
        useRandomProxy: Boolean = false
    ): String = getWithBrowserHeaders(url, timeout, true, useRandomProxy)
}
