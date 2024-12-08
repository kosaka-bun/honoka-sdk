package de.honoka.sdk.util.kotlin.net

import cn.hutool.http.HttpRequest

fun HttpRequest.params(params: Map<String, Any?>): HttpRequest = apply {
    val query = StringBuilder()
    params.entries.forEachIndexed { i, it ->
        query.append("${it.key}=${it.value}")
        if(i < params.size - 1) query.append("&")
    }
    body(query.toString())
}