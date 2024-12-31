package de.honoka.sdk.spring.starter.security

import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil
import de.honoka.sdk.spring.starter.config.SecurityProperties
import de.honoka.sdk.spring.starter.core.context.ApplicationContextHolder.springBean
import de.honoka.sdk.spring.starter.core.web.authorization
import de.honoka.sdk.spring.starter.core.web.get
import de.honoka.sdk.spring.starter.security.token.JwtUtils
import de.honoka.sdk.spring.starter.security.token.TempTokenUtils
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

/**
 * 用于手动为SecurityContextHolder的context添加authentication信息
 */
@Suppress("MemberVisibilityCanBePrivate")
object DefaultAuthorizationFilter : OncePerRequestFilter() {
    
    private val securityProperties = SecurityProperties::class.springBean
    
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val token = request.run { authorization[1] ?: cookies[securityProperties.token.name] }
        val tempToken = request.cookies[securityProperties.token.tempName]
        when {
            !token.isNullOrBlank() -> tokenAuthentication(token)
            !tempToken.isNullOrBlank() -> tempTokenAuthentication(tempToken)
        }
        filterChain.doFilter(request, response)
    }
    
    private fun tokenAuthentication(token: String) {
        val jwt = try {
            JwtUtils.parseAvaliableJwt(token)
        } catch(t: Throwable) {
            return
        }
        val user = JSONUtil.toBean(jwt.payloads["user"] as? JSONObject, DefaultUser::class.java)
        /*
         * 这里必须使用三个参数的UsernamePasswordAuthenticationToken构造方法，因为两个参数的构造方法会
         * 将对象中的authenticated字段设为false，而三个参数的构造方法会设为true。
         */
        val authentication = UsernamePasswordAuthenticationToken(
            user.username, jwt, user.toUserDetails().authorityObjects
        )
        SecurityContextHolder.getContext().authentication = authentication.apply {
            details = user
        }
    }
    
    private fun tempTokenAuthentication(token: String) {
        runCatching {
            TempTokenUtils.checkToken(token)
            SecurityContextHolder.getContext().authentication = TempAuthenticationToken(token)
        }
    }
}

/**
 * 通过临时token获取的临时登录态。
 *
 * 注意：若authenticated被设置为true，则此登录态能够访问到在SecurityConfig中被设置为authenticated
 * 的URL路径，需额外考虑如何避免持有此登录态的用户访问需要普通登录态的URL路径。
 */
@Suppress("unused")
class TempAuthenticationToken(
    val token: String, authenticated: Boolean = false
) : AbstractAuthenticationToken(null) {
    
    init {
        isAuthenticated = authenticated
    }
    
    override fun getCredentials(): Any? = null
    
    override fun getPrincipal(): Any? = null
}