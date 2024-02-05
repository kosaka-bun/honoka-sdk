@file:Suppress("MemberVisibilityCanBePrivate")

package de.honoka.sdk.util.android.server

import android.webkit.MimeTypeMap
import cn.hutool.core.exceptions.ExceptionUtil
import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil
import de.honoka.sdk.util.android.common.GlobalComponents
import de.honoka.sdk.util.framework.web.ApiResponse
import fi.iki.elonen.NanoHTTPD
import java.io.ByteArrayInputStream
import java.io.File

@Suppress("ConstPropertyName")
object HttpServerVariables {

    var webServerPort = 38081

    const val imageUrlPrefix = "/android/img"

    fun getUrlByWebServerPrefix(path: String) = "http://localhost:$webServerPort$path"

    fun getImageUrlByWebServerPrefix(path: String) = getUrlByWebServerPrefix("$imageUrlPrefix$path")
}

class HttpServer(port: Int = HttpServerVariables.webServerPort) : NanoHTTPD(port) {

    companion object {

        lateinit var instance: HttpServer

        private val staticResourcesPrefixes = arrayOf(
            "/assets", "/font", "/img", "/js", "/favicon.ico"
        )

        fun createInstance() {
            HttpServerUtils.initServerPorts()
            instance = HttpServer().apply { start() }
        }

        fun checkOrRestartInstance() {
            if(instance.isAlive) return
            instance.start()
        }
    }

    override fun serve(session: IHTTPSession): Response {
        var path = session.uri
        if(path == "/") path = "/index.html"
        return try {
            handle(path)
        } catch(t: Throwable) {
            errorResponse(t)
        }
    }

    private fun handle(urlPath: String): Response {
        //判断路径是否匹配静态资源前缀
        staticResourcesPrefixes.forEach {
            //加载静态资源
            if(urlPath.startsWith(it)) return staticResourceResponse(urlPath)
        }
        androidImageResponse(urlPath)?.let { return it }
        //加载index.html
        return indexHtmlResponse()
    }

    private fun buildStaticResponse(urlPath: String, content: ByteArray): Response {
        val fileExt = urlPath.substring(urlPath.lastIndexOf(".") + 1)
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExt)
        return newFixedLengthResponse(Response.Status.OK, mimeType, ByteArrayInputStream(content), content.size.toLong())
    }

    private fun staticResourceResponse(urlPath: String): Response {
        val content = GlobalComponents.application.assets.open("web$urlPath").use { it.readBytes() }
        return buildStaticResponse(urlPath, content)
    }

    private fun indexHtmlResponse(): Response {
        val content = GlobalComponents.application.assets.open("web/index.html").use { it.readBytes() }
        return newFixedLengthResponse(Response.Status.OK, MIME_HTML, ByteArrayInputStream(content), content.size.toLong())
    }

    private fun androidImageResponse(urlPath: String): Response? {
        if(!urlPath.startsWith(HttpServerVariables.imageUrlPrefix)) return null
        val filePath = "${GlobalComponents.application.dataDir}${urlPath.substring(HttpServerVariables.imageUrlPrefix.length)}"
        val file = File(filePath)
        if(!file.exists()) return notFoundResponse(filePath)
        return buildStaticResponse(urlPath, file.readBytes())
    }

    private fun notFoundResponse(resourcePath: String): Response = newFixedLengthResponse(
        Response.Status.NOT_FOUND,
        MimeTypeMap.getSingleton().getMimeTypeFromExtension("json"),
        JSONUtil.toJsonPrettyStr(ApiResponse<Any>().apply {
            code = Response.Status.NOT_FOUND.requestStatus
            msg = "$resourcePath is not found"
        })
    )

    private fun errorResponse(t: Throwable): Response = newFixedLengthResponse(
        Response.Status.INTERNAL_ERROR,
        MimeTypeMap.getSingleton().getMimeTypeFromExtension("json"),
        JSONUtil.toJsonPrettyStr(ApiResponse<Any>().apply {
            code = Response.Status.INTERNAL_ERROR.requestStatus
            msg = t.message
            data = JSONObject().also {
                it["stackTrace"] = ExceptionUtil.stacktraceToString(t)
            }
        })
    )
}

object HttpServerUtils {

    private fun getOneAvaliablePort(startPort: Int): Int {
        var port = startPort
        var successful = false
        //验证端口可用性
        for(i in 0 until 10) {
            try {
                HttpServer(port).apply {
                    start()
                    stop()
                }
                successful = true
                break
            } catch(t: Throwable) {
                port += 1
            }
        }
        if(!successful) throw Exception("端口范围（$startPort - ${startPort + 10}）均被占用")
        return port
    }

    fun initServerPorts() {
        HttpServerVariables.webServerPort = getOneAvaliablePort(HttpServerVariables.webServerPort)
    }
}