package de.honoka.sdk.spring.starter.security

import cn.hutool.cache.CacheUtil
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer
import org.springframework.security.core.GrantedAuthority

private typealias AuthorizedUrl = AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizedUrl

private typealias MatcherRegistry = AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry

private val wildcardAuthoritiesCache = CacheUtil.newLFUCache<String, Regex>(100)

private fun String.toWildcardRegex(): Regex = wildcardAuthoritiesCache.run {
    val s = this@toWildcardRegex
    get(s)?.let { return it }
    val rs = split("*").joinToString("[^:]+") {
        if(it.isBlank()) "" else "\\Q$it\\E"
    }
    rs.toRegex().also {
        put(s, it)
    }
}

internal fun Collection<GrantedAuthority>.hasWildcardAuthority(authority: String): Boolean = any {
    it.authority.run {
        if(contains("*")) {
            toWildcardRegex().matches(authority)
        } else {
            this == authority
        }
    }
}

fun AuthorizedUrl.hasWildcardAuthority(authority: String): MatcherRegistry = access { supplier, _ ->
    AuthorizationDecision(supplier.get().authorities.hasWildcardAuthority(authority))
}
