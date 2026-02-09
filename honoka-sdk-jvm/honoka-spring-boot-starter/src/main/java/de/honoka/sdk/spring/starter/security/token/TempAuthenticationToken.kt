package de.honoka.sdk.spring.starter.security.token

import org.springframework.security.authentication.AbstractAuthenticationToken

/**
 * 通过临时token获取的临时登录态。
 *
 * 注意：若authenticated被设置为true，则此登录态能够访问到在SecurityConfig中被设置为authenticated
 * 的URL路径，需额外考虑如何避免持有此登录态的用户访问需要普通登录态的URL路径。
 */
class TempAuthenticationToken(
    val token: String, authenticated: Boolean = false
) : AbstractAuthenticationToken(null) {

    init {
        isAuthenticated = authenticated
    }

    override fun getCredentials(): Any? = null

    override fun getPrincipal(): Any? = null
}
