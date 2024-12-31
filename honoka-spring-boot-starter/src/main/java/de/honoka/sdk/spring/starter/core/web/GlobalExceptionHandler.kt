package de.honoka.sdk.spring.starter.core.web

import cn.hutool.core.exceptions.ExceptionUtil
import de.honoka.sdk.util.kotlin.basic.isAnyType
import de.honoka.sdk.util.kotlin.basic.log
import de.honoka.sdk.util.web.ApiResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.resource.NoResourceFoundException
import kotlin.reflect.KClass

@RestControllerAdvice
class GlobalExceptionHandler {
    
    private val disablePrintLogExceptionTypes = listOf<KClass<out Throwable>>(
        MethodArgumentNotValidException::class,
        NoResourceFoundException::class
    )
    
    @ExceptionHandler
    fun handle(t: Throwable, request: HttpServletRequest, response: HttpServletResponse): ApiResponse<*>? {
        if(!t.isAnyType(disablePrintLogExceptionTypes)) {
            log.error("", t)
        }
        response.status = HttpStatus.INTERNAL_SERVER_ERROR.value()
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
        e: MethodArgumentNotValidException,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ApiResponse<*>? {
        val message = e.allErrors.map { it.defaultMessage }.joinToString()
        return handle(IllegalArgumentException(message), request, response)
    }
}
