package de.honoka.sdk.spring.starter.web.webflux

import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpRequest

fun ServerHttpRequest.canAcceptJson(): Boolean {
    val accept = headers.accept
    if(accept.contains(MediaType.APPLICATION_JSON)) {
        return true
    }
    if(accept.contains(MediaType.ALL)) {
        return !accept.contains(MediaType.TEXT_HTML)
    }
    return false
}
