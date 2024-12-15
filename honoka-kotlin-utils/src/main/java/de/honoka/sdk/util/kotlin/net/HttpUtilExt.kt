package de.honoka.sdk.util.kotlin.net

import cn.hutool.http.HttpUtil

object HttpUtilExt {
    
    fun getWithBrowserHeaders(url: String, timeout: Int? = null): String {
        val request = HttpUtil.createGet(url).apply {
            browserHeaders()
            timeout?.let { timeout(it) }
        }
        return request.execute().body()
    }
}
