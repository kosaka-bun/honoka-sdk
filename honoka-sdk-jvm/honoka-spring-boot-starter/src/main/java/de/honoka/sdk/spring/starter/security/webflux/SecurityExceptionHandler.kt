package de.honoka.sdk.spring.starter.security.webflux

import cn.hutool.core.exceptions.ExceptionUtil
import cn.hutool.json.JSONObject
import de.honoka.sdk.spring.starter.security.SecurityExceptionHandler
import de.honoka.sdk.spring.starter.web.webflux.canAcceptJson
import de.honoka.sdk.util.web.ApiResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.server.ServerAuthenticationEntryPoint
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

object DefaultServerAuthenticationEntryPoint : ServerAuthenticationEntryPoint {

    override fun commence(exchange: ServerWebExchange, ex: AuthenticationException): Mono<Void> = respondError(
        exchange,
        HttpStatus.UNAUTHORIZED,
        SecurityExceptionHandler.Messages.UNAUTHORIZED,
        ex
    )
}

object DefaultServerAccessDeniedHandler : ServerAccessDeniedHandler {

    override fun handle(exchange: ServerWebExchange, denied: AccessDeniedException): Mono<Void> = respondError(
        exchange,
        HttpStatus.FORBIDDEN,
        SecurityExceptionHandler.Messages.FORBIDDEN,
        denied
    )
}

private fun respondError(
    exchange: ServerWebExchange, status: HttpStatusCode, msg: String, exception: Throwable?
): Mono<Void> = exchange.run {
    response.statusCode = status
    if(!request.canAcceptJson()) {
        return response.setComplete()
    }
    response.headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
    val apiResponse = ApiResponse.of<JSONObject>().also { ar ->
        ar.code = status.value()
        ar.success = false
        ar.msg = msg
        ar.data = JSONObject().also { jo ->
            jo["exception"] = ExceptionUtil.getMessage(exception)
        }
    }
    val data = response.bufferFactory().wrap(apiResponse.toJsonString().toByteArray())
    return response.writeWith(Mono.just(data))
}
