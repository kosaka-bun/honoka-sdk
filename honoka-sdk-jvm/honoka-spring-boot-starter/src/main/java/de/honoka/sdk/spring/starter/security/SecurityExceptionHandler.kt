package de.honoka.sdk.spring.starter.security

import de.honoka.sdk.spring.starter.config.WebProperties
import de.honoka.sdk.spring.starter.core.springBeanLazy
import de.honoka.sdk.spring.starter.web.canAcceptJson
import de.honoka.sdk.util.kotlin.text.toJsonString
import de.honoka.sdk.util.kotlin.various.details
import de.honoka.sdk.util.kotlin.various.log
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.access.ExceptionTranslationFilter

internal object SecurityExceptionHandler {

    object Messages {

        const val UNAUTHORIZED = "未登录或Token已失效"

        const val FORBIDDEN = "访问被拒绝"
    }

    private val webProperties by WebProperties::class.springBeanLazy

    fun respondError(
        request: HttpServletRequest, response: HttpServletResponse,
        status: HttpStatus, msg: String, exception: Throwable
    ) {
        response.status = status.value()
        if(!request.canAcceptJson()) return
        response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        response.outputStream.writer(Charsets.UTF_8).use {
            val apiResponse = exception.details.toApiResponse(3).apply {
                code = status.value()
                this.msg = msg
                if(webProperties.returnStackTraceOnError) {
                    SecurityExceptionHandler.log.error("", exception)
                } else {
                    error = null
                }
            }
            it.write(apiResponse.toJsonString())
        }
    }
}

/**
 * 当[ExceptionTranslationFilter]之后存在`Filter`抛出[AccessDeniedException]时，[ExceptionTranslationFilter]
 * 会检查[SecurityContextHolder.context]中是否存在`authentication`信息。若不存在，则视为请求方未登录，调用本类中
 * 的方法对请求和响应进行处理。
 *
 * 此处为返回一段JSON提示信息。
 */
object DefaultAuthenticationEntryPoint : AuthenticationEntryPoint {

    override fun commence(
        request: HttpServletRequest, response: HttpServletResponse, e: AuthenticationException
    ) {
        SecurityExceptionHandler.respondError(
            request, response, HttpStatus.UNAUTHORIZED,
            SecurityExceptionHandler.Messages.UNAUTHORIZED, e
        )
    }
}

/**
 * 当[ExceptionTranslationFilter]之后存在`Filter`抛出[AccessDeniedException]时，[ExceptionTranslationFilter]
 * 会检查[SecurityContextHolder.context]中是否存在`authentication`信息。若存在，则视为请求方已登录但无权访问
 * 指定的路径，调用本类中的方法对请求和响应进行处理。
 *
 * 此处为返回一段JSON提示信息。
 */
object DefaultAccessDeniedHandler : AccessDeniedHandler {

    override fun handle(
        request: HttpServletRequest, response: HttpServletResponse, e: AccessDeniedException
    ) {
        SecurityExceptionHandler.respondError(
            request, response, HttpStatus.FORBIDDEN,
            SecurityExceptionHandler.Messages.FORBIDDEN, e
        )
    }
}
