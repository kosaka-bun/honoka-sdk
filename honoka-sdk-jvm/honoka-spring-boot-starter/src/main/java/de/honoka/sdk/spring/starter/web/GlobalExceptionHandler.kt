package de.honoka.sdk.spring.starter.web

import cn.hutool.core.exceptions.ExceptionUtil
import de.honoka.sdk.spring.starter.config.MainConfig
import de.honoka.sdk.util.kotlin.lang.isAny
import de.honoka.sdk.util.kotlin.lang.log
import de.honoka.sdk.util.web.ApiResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.resource.NoResourceFoundException
import kotlin.reflect.KClass

@RestControllerAdvice(name = "${MainConfig.STARTER_BEAN_NAME_PREFIX}GlobalExceptionHandler")
class GlobalExceptionHandler {
    
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
        if(!t.isAny(disablePrintLogExceptionTypes)) {
            log.error("", t)
        }
        response.status = status.value()
        if(!request.canAcceptJson()) return null
        val msg = if(t.message?.isNotBlank() == true) {
            t.message
        } else {
            ExceptionUtil.getMessage(t)
        }
        return ApiResponse.fail(msg)
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
