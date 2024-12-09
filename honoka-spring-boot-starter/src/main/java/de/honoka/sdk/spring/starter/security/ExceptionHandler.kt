package de.honoka.sdk.spring.starter.security

import cn.hutool.core.exceptions.ExceptionUtil
import cn.hutool.json.JSONObject
import de.honoka.sdk.spring.starter.security.ExceptionHandler.Companion.respondError
import de.honoka.sdk.util.web.ApiResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionHandler {
    
    companion object {
        
        fun HttpServletResponse.respondError(status: HttpStatus, msg: String, exception: Throwable?) {
            this.status = status.value()
            addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            outputStream.writer(Charsets.UTF_8).use {
                val apiResponse = ApiResponse.of<JSONObject>().also { ar ->
                    ar.code = status.value()
                    ar.status = false
                    ar.msg = msg
                    ar.data = JSONObject().also { jo ->
                        jo["exception"] = ExceptionUtil.getMessage(exception)
                    }
                }
                it.write(apiResponse.toJsonString())
            }
        }
    }
    
    @ExceptionHandler
    fun handle(e: AccessDeniedException, request: HttpServletRequest, response: HttpServletResponse) = run {
        AccessDeniedHandlerImpl.handle(request, response, e)
    }
}

/**
 * 当ExceptionTranslationFilter之后存在Filter抛出AccessDeniedException时，ExceptionTranslationFilter
 * 会检查SecurityContextHolder的context中是否存在authentication信息，若不存在，则视为请求方未登录，调用
 * 本类中的方法对请求和响应进行处理。
 * 此处为返回一段JSON提示信息。
 */
object AuthenticationEntryPointImpl : AuthenticationEntryPoint {

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException?
    ) = response.respondError(HttpStatus.UNAUTHORIZED, "未登录或Token已失效", authException)
}

/**
 * 当ExceptionTranslationFilter之后存在Filter抛出AccessDeniedException时，ExceptionTranslationFilter
 * 会检查SecurityContextHolder的context中是否存在authentication信息，若存在，则视为请求方已登录但无权访问
 * 指定的路径，调用本类中的方法对请求和响应进行处理。
 * 此处为返回一段JSON提示信息。
 */
object AccessDeniedHandlerImpl : AccessDeniedHandler {

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException?
    ) = response.respondError(HttpStatus.FORBIDDEN, "访问被拒绝", accessDeniedException)
}