package de.honoka.sdk.spring.starter.security.token

import cn.hutool.cache.CacheUtil
import cn.hutool.core.lang.Assert
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * 临时token工具，通常用于对外提供分享链接时，供使用分享链接的匿名用户使用
 */
@Suppress("unused")
object TempTokenUtils {
    
    private val tokenCache = CacheUtil.newTimedCache<String, String?>(0).apply {
        schedulePrune(TimeUnit.HOURS.toMillis(1))
    }
    
    fun newToken(periodHours: Long = 1): String {
        val token = UUID.randomUUID().toString()
        val timeout = TimeUnit.HOURS.toMillis(periodHours)
        tokenCache.put(token, null, timeout)
        return token
    }
    
    fun checkToken(token: String) {
        Assert.isTrue(tokenCache.containsKey(token), "token不存在或已过期")
    }
    
    fun cancelToken(token: String) = tokenCache.remove(token)
}