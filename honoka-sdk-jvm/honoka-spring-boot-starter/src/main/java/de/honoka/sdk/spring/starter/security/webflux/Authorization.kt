package de.honoka.sdk.spring.starter.security.webflux

import de.honoka.sdk.spring.starter.security.hasWildcardAuthority
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.config.web.server.ServerHttpSecurity

private typealias AuthorizeExchangeSpec = ServerHttpSecurity.AuthorizeExchangeSpec

private typealias Access = ServerHttpSecurity.AuthorizeExchangeSpec.Access

fun Access.hasWildcardAuthority(authority: String): AuthorizeExchangeSpec = access { mono, _ ->
    mono.map {
        AuthorizationDecision(it.authorities.hasWildcardAuthority(authority))
    }
}
