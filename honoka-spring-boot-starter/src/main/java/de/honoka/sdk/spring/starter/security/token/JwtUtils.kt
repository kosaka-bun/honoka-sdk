package de.honoka.sdk.spring.starter.security.token

import cn.hutool.cache.CacheUtil
import cn.hutool.core.bean.BeanUtil
import cn.hutool.core.date.DateField
import cn.hutool.core.date.DateTime
import cn.hutool.jwt.JWT
import de.honoka.sdk.spring.starter.config.property.SecurityProperties
import de.honoka.sdk.spring.starter.core.context.ApplicationContextHolder.Companion.springBean
import de.honoka.sdk.spring.starter.security.DefaultUser
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.rememberme.InvalidCookieException
import java.util.concurrent.TimeUnit

object JwtUtils {
    
    @Suppress("MemberVisibilityCanBePrivate")
    var key: String = SecurityProperties::class.springBean.jwtKey
    
    private val tokenCache = CacheUtil.newTimedCache<String, String?>(0).apply {
        /**
         * 设置间隔多长时间主动清理过期缓存，防止过期缓存长时间未读取而滞留在内存中。
         *
         * 需要注意的是，并不是过期缓存没有被主动清理就意味着缓存没有过期，每次从缓存中取值时，即使取到了
         * 值也会再次检查取到的值是否已过期，如果已过期将会立刻清理此值。
         * @see cn.hutool.cache.impl.StampedCache.get
         */
        schedulePrune(TimeUnit.HOURS.toMillis(1))
    }
    
    private val JWT.cacheKey
        get() = "${payloads.getByPath("user.id")}-${payloads["iat"]}"
    
    fun newJwt(user: DefaultUser, periodDays: Int = 7): String = JWT.create().run {
        setKey(key.toByteArray())
        val payload = mapOf("user" to BeanUtil.beanToMap(user))
        addPayloads(payload)
        val now = DateTime.now()
        val expireAt = now.offsetNew(DateField.DAY_OF_YEAR, periodDays)
        setIssuedAt(now)
        setNotBefore(now)
        setExpiresAt(expireAt)
        val timeout = expireAt.time - now.time
        tokenCache.put("${user.id}-${now.time / 1000L}", null, timeout)
        sign()
    }
    
    fun parseAvaliableJwt(token: String): JWT = JWT(token).run {
        setKey(key.toByteArray())
        val avaliable = validate(0) && tokenCache.containsKey(cacheKey)
        if(!avaliable) throw InvalidCookieException("JWT无效或已过期")
        this
    }
    
    fun cancelJwt() {
        val jwt = SecurityContextHolder.getContext().authentication.credentials as JWT
        tokenCache.remove(jwt.cacheKey)
    }
}