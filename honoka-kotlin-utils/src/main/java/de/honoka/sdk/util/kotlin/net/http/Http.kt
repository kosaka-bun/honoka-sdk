package de.honoka.sdk.util.kotlin.net.http

import cn.hutool.http.HttpRequest
import cn.hutool.json.JSONObject
import de.honoka.sdk.util.kotlin.net.proxy.ProxyPool
import de.honoka.sdk.util.kotlin.text.toJsonObject
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.jvm.javaField

internal val browserHeadersCache = ConcurrentHashMap<String, JSONObject>()

fun HttpRequest.params(params: Map<String, Any?>) {
    val query = StringBuilder()
    params.entries.forEachIndexed { i, it ->
        query.append("${it.key}=${it.value}")
        if(i < params.size - 1) query.append("&")
    }
    body(query.toString())
}

internal fun HttpRequest.browserHeaders(headersFileName: String) {
    val json = browserHeadersCache[headersFileName] ?: run {
        val path = "/http/static-headers/${headersFileName}.json"
        val declaringClass = ::browserHeadersCache.javaField!!.declaringClass
        declaringClass.getResource(path)!!.readText().toJsonObject()
    }
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

fun HttpRequest.randomProxy(autoInitPool: Boolean = true) {
    if(autoInitPool) ProxyPool.instance.init()
    val proxy = ProxyPool.instance.randomProxy?.split(":")
    proxy?.let { setHttpProxy(proxy[0], proxy[1].toInt()) }
}