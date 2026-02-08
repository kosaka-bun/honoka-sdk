package de.honoka.sdk.spring.starter.web

import de.honoka.sdk.spring.starter.config.WebProperties
import de.honoka.sdk.util.kotlin.text.isNotBlank
import de.honoka.sdk.util.kotlin.various.details
import de.honoka.sdk.util.kotlin.various.isAny
import de.honoka.sdk.util.kotlin.various.log
import de.honoka.sdk.util.kotlin.web.ApiResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.resource.NoResourceFoundException
import kotlin.reflect.KClass

@RestControllerAdvice
class GlobalExceptionHandler(private val webProperties: WebProperties) {
    
    private val disablePrintLogExceptionTypes = listOf<KClass<out Throwable>>(
        MethodArgumentNotValidException::class,
        NoResourceFoundException::class
    )

    private fun handleDefault(
        t: Throwable,
        request: HttpServletRequest,
        response: HttpServletResponse,
        status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR
    ): ApiResponse<*>? {
        if(webProperties.returnStackTraceOnError || !t.isAny(disablePrintLogExceptionTypes)) {
            this@GlobalExceptionHandler.log.error("", t)
        }
        response.status = status.value()
        if(!request.canAcceptJson()) return null
        val result = t.details.toApiResponse(3)
        if(!webProperties.returnStackTraceOnError) {
            result.msg = t.message.takeIf { it.isNotBlank() } ?: "内部服务器错误"
            result.error = null
        }
        return result
    }

    @ExceptionHandler
    fun handle(
        t: Throwable,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ApiResponse<*>? = handleDefault(t, request, response)
    
    @ExceptionHandler
    fun handle(
        e: MethodArgumentNotValidException,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ApiResponse<*>? {
        val message = e.allErrors.joinToString { it.defaultMessage.toString() }
        return handleDefault(IllegalArgumentException(message), request, response)
    }

    @ExceptionHandler
    fun handle(
        e: NoResourceFoundException,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ApiResponse<*>? = handleDefault(e, request, response, HttpStatus.NOT_FOUND)
}
