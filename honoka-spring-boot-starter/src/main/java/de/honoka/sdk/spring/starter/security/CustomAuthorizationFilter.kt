package de.honoka.sdk.spring.starter.security

import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil
import de.honoka.sdk.spring.starter.config.property.SecurityProperties
import de.honoka.sdk.spring.starter.core.ApplicationContextHolder.Companion.springBean
import de.honoka.sdk.spring.starter.core.web.WebUtils.authorization
import de.honoka.sdk.spring.starter.core.web.WebUtils.get
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
object CustomAuthorizationFilter : OncePerRequestFilter() {
    
    private val securityProperties = SecurityProperties::class.springBean
    
    var tokenName = "token"
    
    var tempTokenName = "temp-token"
    
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val token = request.run { authorization[1] ?: cookies[tokenName] }
        val tempToken = request.cookies[tempTokenName]
        when {
            !token.isNullOrBlank() -> tokenAuthentication(token)
            !tempToken.isNullOrBlank() -> tempTokenAuthentication(tempToken)
        }
        filterChain.doFilter(request, response)
    }
    
    private fun tokenAuthentication(token: String) {
        val jwt = try {
            JwtUtils.parseAvaliableJwt(token, securityProperties.jwtKey)
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

@Suppress("unused")
class TempAuthenticationToken(val token: String) : AbstractAuthenticationToken(null) {
    
    init {
        isAuthenticated = true
    }
    
    override fun getCredentials(): Any? = null
    
    override fun getPrincipal(): Any? = null
}