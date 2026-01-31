package de.honoka.sdk.spring.starter.security

import cn.hutool.core.exceptions.ExceptionUtil
import cn.hutool.json.JSONObject
import de.honoka.sdk.spring.starter.web.canAcceptJson
import de.honoka.sdk.spring.starter.web.webflux.canAcceptJson
import de.honoka.sdk.util.web.ApiResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.access.ExceptionTranslationFilter
import org.springframework.security.web.server.ServerAuthenticationEntryPoint
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@RestControllerAdvice
class SecurityExceptionHandler {
    
    @ExceptionHandler
    fun handle(e: AccessDeniedException, request: HttpServletRequest, response: HttpServletResponse) {
        DefaultAccessDeniedHandler.handle(request, response, e)
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
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException?
    ) {
        respondError(
            request, response,
            HttpStatus.UNAUTHORIZED, Messages.UNAUTHORIZED,
            authException
        )
    }
}

object DefaultServerAuthenticationEntryPoint : ServerAuthenticationEntryPoint {

    override fun commence(exchange: ServerWebExchange, ex: AuthenticationException): Mono<Void> =
        respondError(exchange, HttpStatus.UNAUTHORIZED, Messages.UNAUTHORIZED, ex)
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
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException?
    ) {
        respondError(
            request, response,
            HttpStatus.FORBIDDEN, Messages.FORBIDDEN,
            accessDeniedException
        )
    }
}

object DefaultServerAccessDeniedHandler : ServerAccessDeniedHandler {

    override fun handle(exchange: ServerWebExchange, denied: AccessDeniedException): Mono<Void> =
        respondError(exchange, HttpStatus.FORBIDDEN, Messages.FORBIDDEN, denied)
}

private object Messages {

    const val UNAUTHORIZED = "未登录或Token已失效"

    const val FORBIDDEN = "访问被拒绝"
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
            ar.success = false
            ar.msg = msg
            ar.data = JSONObject().also { jo ->
                jo["exception"] = ExceptionUtil.getMessage(exception)
            }
        }
        it.write(apiResponse.toJsonString())
    }
}

private fun respondError(
    exchange: ServerWebExchange, status: HttpStatusCode, msg: String, exception: Throwable?
): Mono<Void> = exchange.run {
    response.statusCode = status
    if(!request.canAcceptJson()) {
        return response.setComplete()
    }
    response.headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
    val apiResponse = ApiResponse.of<JSONObject>().also { ar ->
        ar.code = status.value()
        ar.success = false
        ar.msg = msg
        ar.data = JSONObject().also { jo ->
            jo["exception"] = ExceptionUtil.getMessage(exception)
        }
    }
    val data = response.bufferFactory().wrap(apiResponse.toJsonString().toByteArray())
    return response.writeWith(Mono.just(data))
}
