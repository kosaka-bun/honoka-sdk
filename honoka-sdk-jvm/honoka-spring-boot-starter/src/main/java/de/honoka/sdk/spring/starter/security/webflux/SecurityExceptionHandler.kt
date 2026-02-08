package de.honoka.sdk.spring.starter.security.webflux

import de.honoka.sdk.spring.starter.config.WebFluxProperties
import de.honoka.sdk.spring.starter.core.springBeanLazy
import de.honoka.sdk.spring.starter.security.SecurityExceptionHandler.Messages
import de.honoka.sdk.spring.starter.web.webflux.canAcceptJson
import de.honoka.sdk.util.kotlin.text.toJsonString
import de.honoka.sdk.util.kotlin.various.ExceptionDetails
import de.honoka.sdk.util.kotlin.various.details
import de.honoka.sdk.util.kotlin.various.log
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

private object SecurityExceptionHandler {

    val webFluxProperties by WebFluxProperties::class.springBeanLazy

    fun respondError(
        exchange: ServerWebExchange, status: HttpStatusCode, msg: String, exception: Throwable
    ): Mono<Void> = exchange.run {
        response.statusCode = status
        if(!request.canAcceptJson()) {
            return response.setComplete()
        }
        response.headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        val apiResponse = exception.details.toApiResponse(3).apply {
            val rawMsg = this.msg
            this.msg = msg
            (error as ExceptionDetails).run {
                message = rawMsg
                if(!webFluxProperties.returnStackTraceOnError) {
                    SecurityExceptionHandler.log.error("", exception)
                    stackTrace = null
                }
            }
        }
        val data = response.bufferFactory().wrap(apiResponse.toJsonString().toByteArray())
        return response.writeWith(Mono.just(data))
    }
}

object DefaultServerAuthenticationEntryPoint : ServerAuthenticationEntryPoint {

    override fun commence(exchange: ServerWebExchange, ex: AuthenticationException): Mono<Void> {
        return SecurityExceptionHandler.respondError(
            exchange, HttpStatus.UNAUTHORIZED,
            Messages.UNAUTHORIZED, ex
        )
    }
}

object DefaultServerAccessDeniedHandler : ServerAccessDeniedHandler {

    override fun handle(exchange: ServerWebExchange, denied: AccessDeniedException): Mono<Void> {
        return SecurityExceptionHandler.respondError(
            exchange, HttpStatus.FORBIDDEN,
            Messages.FORBIDDEN, denied
        )
    }
}
