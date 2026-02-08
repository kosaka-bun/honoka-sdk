package de.honoka.sdk.spring.starter.security

import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil
import de.honoka.sdk.spring.starter.config.SecurityProperties
import de.honoka.sdk.spring.starter.core.springBeanLazy
import de.honoka.sdk.spring.starter.security.token.JwtUtils
import de.honoka.sdk.spring.starter.security.token.TempAuthenticationToken
import de.honoka.sdk.spring.starter.security.token.TempTokenUtils
import de.honoka.sdk.spring.starter.web.authorization
import de.honoka.sdk.spring.starter.web.get
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

/**
 * 用于手动为SecurityContextHolder的context添加authentication信息
 */
@Suppress("MemberVisibilityCanBePrivate")
object DefaultAuthorizationFilter : OncePerRequestFilter() {
    
    private val securityProperties by SecurityProperties::class.springBeanLazy

    override fun doFilterInternal(
        request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain
    ) {
        val token = request.authorization[1]
        val tempToken = request.cookies[securityProperties.jwt.tempName]
        when {
            !token.isNullOrBlank() -> tokenAuthentication(token)
            !tempToken.isNullOrBlank() -> tempTokenAuthentication(tempToken)
        }
        filterChain.doFilter(request, response)
    }
    
    private fun tokenAuthentication(token: String) {
        val jwt = runCatching {
            JwtUtils.parseAvaliableJwt(token)
        }.getOrElse {
            return
        }
        val user = JSONUtil.toBean(jwt.payloads["user"] as JSONObject, DefaultUser::class.java)
        /*
         * 这里必须使用三个参数的UsernamePasswordAuthenticationToken构造方法，因为两个参数的构造方法会
         * 将对象中的authenticated字段设为false，而三个参数的构造方法会设为true。
         */
        val authentication = UsernamePasswordAuthenticationToken(
            user.id, jwt, user.toUserDetails().authorities
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
