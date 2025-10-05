package de.honoka.sdk.spring.starter.security

import cn.hutool.core.exceptions.ExceptionUtil
import cn.hutool.json.JSONObject
import de.honoka.sdk.spring.starter.core.web.canAcceptJson
import de.honoka.sdk.util.web.ApiResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class SecurityExceptionHandler {
    
    @ExceptionHandler
    fun handle(e: AccessDeniedException, request: HttpServletRequest, response: HttpServletResponse) {
        AccessDeniedHandlerImpl.handle(request, response, e)
    }
}

/**
 * 当`ExceptionTranslationFilter`之后存在Filter抛出`AccessDeniedException`时，`ExceptionTranslationFilter`
 * 会检查`SecurityContextHolder`的`context`中是否存在`authentication`信息，若不存在，则视为请求方未登录，调用
 * 本类中的方法对请求和响应进行处理。
 * 此处为返回一段JSON提示信息。
 */
object AuthenticationEntryPointImpl : AuthenticationEntryPoint {

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException?
    ) {
        respondError(request, response, HttpStatus.UNAUTHORIZED, "未登录或Token已失效", authException)
    }
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
    ) {
        respondError(request, response, HttpStatus.FORBIDDEN, "访问被拒绝", accessDeniedException)
    }
}

private fun respondError(
    request: HttpServletRequest, response: HttpServletResponse,
    status: HttpStatus, msg: String, exception: Throwable?
) {
    response.status = status.value()
    if(!request.canAcceptJson()) return
    response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
    response.outputStream.writer(Charsets.UTF_8).use {
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
