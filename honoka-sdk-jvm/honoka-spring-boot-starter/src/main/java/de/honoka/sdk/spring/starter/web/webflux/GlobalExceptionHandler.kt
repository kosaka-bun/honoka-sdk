package de.honoka.sdk.spring.starter.web.webflux

import de.honoka.sdk.spring.starter.config.WebFluxProperties
import de.honoka.sdk.spring.starter.core.springBeanLazy
import de.honoka.sdk.util.kotlin.text.isNotBlank
import de.honoka.sdk.util.kotlin.text.toJsonString
import de.honoka.sdk.util.kotlin.various.details
import de.honoka.sdk.util.kotlin.various.isAny
import de.honoka.sdk.util.kotlin.various.log
import de.honoka.sdk.util.kotlin.web.ApiResponse
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

        private val webFluxProperties by WebFluxProperties::class.springBeanLazy

        private val disablePrintLogExceptionTypes = listOf<KClass<out Throwable>>(
            MethodArgumentNotValidException::class,
            ResponseStatusException::class
        )

        internal fun handleDefault(
            t: Throwable,
            exchange: ServerWebExchange,
            status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR
        ): ApiResponse<*>? = exchange.run {
            if(webFluxProperties.returnStackTraceOnError || !t.isAny(disablePrintLogExceptionTypes)) {
                GlobalExceptionHandler::class.log.error("", t)
            }
            response.statusCode = HttpStatusCode.valueOf(status.value())
            if(!request.canAcceptJson()) return null
            val result = t.details.toApiResponse(3)
            if(!webFluxProperties.returnStackTraceOnError) {
                result.msg = t.message.takeIf { it.isNotBlank() } ?: "内部服务器错误"
                result.error = null
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
        response.headers.contentType = MediaType.APPLICATION_JSON
        val result = GlobalExceptionHandler.handleDefault(ex, exchange, status)
        val buffer = response.bufferFactory().wrap(result!!.toJsonString().toByteArray())
        response.writeWith(Mono.just(buffer))
    }
}
