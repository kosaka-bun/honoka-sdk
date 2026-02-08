package de.honoka.sdk.util.kotlin.web

import cn.hutool.http.HttpStatus

@Suppress("UNCHECKED_CAST")
data class ApiResponse<T>(

    var code: Int? = null,

    var success: Boolean? = null,

    var msg: String? = null,

    var data: T = null as T,

    var error: Any? = null
) {

    companion object {

        fun empty(): ApiResponse<Any?> = ApiResponse()

        fun <T> of(): ApiResponse<T> = ApiResponse()

        fun <T> success(msg: String?, data: T): ApiResponse<T> = ApiResponse(
            HttpStatus.HTTP_OK, true, msg, data
        )

        fun <T> success(data: T): ApiResponse<T> = success(null, data)

        fun success(): ApiResponse<Any?> = success(null)

        fun fail(code: Int?, msg: String?, error: Any?): ApiResponse<Any?> = ApiResponse(
            code, false, msg, error = error
        )

        fun fail(msg: String? = null, error: Any? = null): ApiResponse<Any?> = fail(
            HttpStatus.HTTP_INTERNAL_ERROR, msg, error
        )
    }
}
