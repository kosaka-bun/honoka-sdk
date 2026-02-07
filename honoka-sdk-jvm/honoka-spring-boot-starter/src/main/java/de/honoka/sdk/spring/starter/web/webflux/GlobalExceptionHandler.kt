package de.honoka.sdk.spring.starter.web.webflux

import cn.hutool.core.exceptions.ExceptionUtil
import de.honoka.sdk.spring.starter.config.WebFluxProperties
import de.honoka.sdk.spring.starter.core.springBean
import de.honoka.sdk.util.kotlin.various.ExceptionDetails
import de.honoka.sdk.util.kotlin.various.isAny
import de.honoka.sdk.util.kotlin.various.log
import de.honoka.sdk.util.web.ApiResponse
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebExceptionHandler
import reactor.core.publisher.Mono
import kotlin.reflect.KClass

@NotForGateway
@RestControllerAdvice
class GlobalExceptionHandler {

    companion object {

        private val webFluxProperties by lazy { WebFluxProperties::class.springBean }

        private val disablePrintLogExceptionTypes = listOf<KClass<out Throwable>>(
            MethodArgumentNotValidException::class,
            ResponseStatusException::class
        )

        internal fun handleDefault(
            t: Throwable,
            exchange: ServerWebExchange,
            status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR
        ): ApiResponse<*>? = exchange.run {
            if(!t.isAny(disablePrintLogExceptionTypes)) {
                log.error("", t)
            }
            response.statusCode = HttpStatusCode.valueOf(status.value())
            if(!request.canAcceptJson()) return null
            response.headers.contentType = MediaType.APPLICATION_JSON
            val msg = if(t.message?.isNotBlank() == true) {
                t.message
            } else {
                ExceptionUtil.getMessage(t)
            }
            val result = ApiResponse.fail(msg)
            if(webFluxProperties.returnStackTraceOnError) {
                result.data = ExceptionDetails(t)
            }
            result
        }
    }

    @ExceptionHandler
    fun handle(
        t: Throwable,
        exchange: ServerWebExchange,
    ): ApiResponse<*>? = handleDefault(t, exchange)

    @ExceptionHandler
    fun handle(
        e: MethodArgumentNotValidException,
        exchange: ServerWebExchange,
    ): ApiResponse<*>? {
        val message = e.allErrors.joinToString { it.defaultMessage.toString() }
        return handleDefault(IllegalArgumentException(message), exchange)
    }

    @ExceptionHandler
    fun handle(
        e: ResponseStatusException,
        exchange: ServerWebExchange,
    ): ApiResponse<*>? = handleDefault(
        e, exchange, HttpStatus.valueOf(e.statusCode.value())
    )
}

@ForGateway
@Order(-1)
@Component
class GatewayExceptionHandler : WebExceptionHandler {

    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> = exchange.run {
        val status = response.statusCode.run {
            if(this == null || is2xxSuccessful) {
                HttpStatus.INTERNAL_SERVER_ERROR
            } else {
                HttpStatus.valueOf(value())
            }
        }
        val result = GlobalExceptionHandler.handleDefault(ex, exchange, status)
        val buffer = response.bufferFactory().wrap(result!!.toJsonString().toByteArray())
        response.writeWith(Mono.just(buffer))
    }
}
