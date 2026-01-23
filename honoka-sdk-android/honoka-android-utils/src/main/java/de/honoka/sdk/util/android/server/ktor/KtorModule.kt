package de.honoka.sdk.util.android.server.ktor

import android.util.Log
import android.webkit.MimeTypeMap
import cn.hutool.core.exceptions.ExceptionUtil
import cn.hutool.json.JSONObject
import de.honoka.sdk.util.android.basic.global
import de.honoka.sdk.util.android.jsinterface.JsInterfaceRegistrar
import de.honoka.sdk.util.android.server.DefaultHttpServer
import de.honoka.sdk.util.android.server.HttpServer
import de.honoka.sdk.util.android.server.StatusPageHandler
import de.honoka.sdk.util.android.server.respondJson
import de.honoka.sdk.util.web.ApiResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException

internal object KtorModule {

    fun getModule(options: HttpServer.Options): Application.() -> Unit = {
        routing {
            options.customRoutings.forEach {
                it()
            }
            RequestMappingsRegistrar(this).run {
                root()
                staticResource()
                androidImage()
                jsInterface()
                other()
            }
        }
        install(StatusPages) {
            StatusHandlerRegistrar(this).run {
                notFound()
                notFoundException()
                exception()
            }
        }
        //https://ktor.io/docs/server-cors.html
        install(CORS) {
            anyHost()
            HttpMethod.DefaultMethods.forEach {
                allowMethod(it)
            }
            HttpHeaders.run {
                listOf(ContentType, Authorization).forEach {
                    allowHeader(it)
                }
            }
        }
    }
}

private class RequestMappingsRegistrar(private val routing: Routing) {

    private fun guessContentType(path: String): ContentType {
        val defaultType = ContentType.Application.OctetStream
        val pointLastIndex = path.lastIndexOf(".")
        if(pointLastIndex < 0 || pointLastIndex == path.lastIndex) {
            return defaultType
        }
        val fileExt = path.substring(pointLastIndex + 1)
        val mimeType = runCatching {
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExt)?.let {
                ContentType.parse(it)
            } ?: defaultType
        }.getOrDefault(defaultType)
        return mimeType
    }

    private suspend fun RoutingContext.respondRoot() {
        respondAsset("web/index.html")
    }

    private suspend fun RoutingContext.respondAsset(path: String, contentType: ContentType? = null) {
        val content = global.assets.runCatching {
            open(path).use { it.readBytes() }
        }.getOrElse {
            when(it) {
                is FileNotFoundException -> throw NotFoundException()
                else -> throw it
            }
        }
        call.respondBytes(content, contentType ?: guessContentType(path))
    }

    private suspend fun RoutingContext.respondFile(path: String, contentType: ContentType? = null) {
        val content = File(path).run {
            if(!exists()) throw NotFoundException()
            readBytes()
        }
        call.respondBytes(content, contentType ?: guessContentType(path))
    }

    fun root() {
        listOf("/", "/index.html").forEach {
            routing.get(it) {
                respondRoot()
            }
        }
    }

    fun staticResource() {
        DefaultHttpServer.staticResourcesPrefixes.forEach {
            val mappingPath = if(it.contains(".")) it else "$it/{...}"
            routing.get(mappingPath) {
                respondAsset("web${call.request.path()}")
            }
        }
    }

    fun androidImage() {
        val prefix = DefaultHttpServer.IMAGE_URL_PREFIX
        fun handler(subPath: String): RoutingHandler = {
            val imagePath = call.request.path().removePrefix("$prefix/$subPath")
            val filePath = "${global.application.dataDir}/$subPath/image$imagePath"
            respondFile(filePath)
        }
        routing.get("$prefix/files/{...}", handler("files"))
        routing.get("$prefix/cache/{...}", handler("cache"))
    }

    fun jsInterface() {
        routing.post("/jsInterface/{...}") {
            val path = call.request.path().removePrefix("/jsInterface/").split("/")
            withContext(DefaultHttpServer.coroutineDispatcher) {
                try {
                    val result = JsInterfaceRegistrar.invokeAsyncMethod(
                        path[0], path[1], call.receiveText()
                    )
                    call.respondJson(ApiResponse.success(result))
                } catch(t: Throwable) {
                    val throwable = ExceptionUtil.getRootCause(t)
                    Log.e("jsInterface", "", throwable)
                    throw throwable
                }
            }
        }
    }

    fun other() {
        routing.get("/{...}") {
            /*
             * 直接返回主页内容，不进行重定向。
             * 许多前端应用会使用URL请求路径作为Router路径，如果需要能不经过主页就直接跳转到指定Router
             * 路径的页面，则应当在URL请求不匹配已定义的路径的情况下，返回主页内容，这样Web应用就能在
             * 主页加载完成后立刻读取到URL路径，然后进行RouterView的更新。
             */
            respondRoot()
        }
    }
}

private class StatusHandlerRegistrar(private val config: StatusPagesConfig) {

    private val notFoundHandler: StatusPageHandler = { call, status ->
        val res = ApiResponse.fail(status.value, "path: ${call.request.path()}")
        call.respondJson(res, status)
    }

    fun notFound() {
        config.status(HttpStatusCode.NotFound, handler = notFoundHandler)
    }

    fun notFoundException() {
        config.exception<NotFoundException> { call, _ ->
            notFoundHandler(call, HttpStatusCode.NotFound)
        }
    }

    fun exception() {
        config.exception<Throwable> { call, t ->
            val realException = ExceptionUtil.getRootCause(t)
            val status = HttpStatusCode.InternalServerError
            val res = ApiResponse.of<Any>().apply {
                code = status.value
                this.success = false
                msg = ExceptionUtil.getMessage(realException)
                data = JSONObject().also {
                    it["stackTrace"] = ExceptionUtil.stacktraceToString(realException)
                }
            }
            call.respondJson(res, status)
        }
    }
}
