package de.honoka.sdk.spring.starter.core.web

import cn.hutool.core.exceptions.ExceptionUtil
import de.honoka.sdk.util.kotlin.basic.log
import de.honoka.sdk.util.web.ApiResponse
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    
    private fun handle(t: Throwable, response: HttpServletResponse, printLog: Boolean): ApiResponse<*> {
        if(printLog) log.error("", t)
        response.status = HttpStatus.INTERNAL_SERVER_ERROR.value()
        val msg = if(t.message?.isNotBlank() == true) t.message else ExceptionUtil.getMessage(t)
        return ApiResponse.fail(msg)
    }
    
    @ExceptionHandler
    fun handle(t: Throwable, response: HttpServletResponse): ApiResponse<*> = handle(t, response, true)
    
    @ExceptionHandler
    fun handle(e: MethodArgumentNotValidException, response: HttpServletResponse): ApiResponse<*> {
        val message = e.allErrors.map { it.defaultMessage }.joinToString()
        return handle(IllegalArgumentException(message), response, false)
    }
}