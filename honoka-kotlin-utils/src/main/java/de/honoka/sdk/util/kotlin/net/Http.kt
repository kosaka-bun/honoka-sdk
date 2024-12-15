package de.honoka.sdk.util.kotlin.net

import cn.hutool.http.HttpRequest
import de.honoka.sdk.util.kotlin.text.toJsonObject

fun HttpRequest.params(params: Map<String, Any?>) {
    val query = StringBuilder()
    params.entries.forEachIndexed { i, it ->
        query.append("${it.key}=${it.value}")
        if(i < params.size - 1) query.append("&")
    }
    body(query.toString())
}

internal fun HttpRequest.browserHeaders(headersFileName: String) {
    val path = "/http/static-headers/${headersFileName}.json"
    val json = object {}::class.java.getResource(path)!!.readText().toJsonObject()
    json.forEach { k, v ->
        header(k, v?.toString() ?: "", true)
    }
}

fun HttpRequest.browserHeaders() {
    browserHeaders("document")
}

fun HttpRequest.browserApiHeaders() {
    browserHeaders("api")
}
