package de.honoka.sdk.util.android.web.server

import cn.hutool.core.io.FileUtil
import de.honoka.sdk.util.android.various.global
import de.honoka.sdk.util.android.web.server.ktor.KtorModule
import de.honoka.sdk.util.android.web.server.ktor.RoutingDefinition
import de.honoka.sdk.util.concurrent.ThreadPoolUtils
import de.honoka.sdk.util.kotlin.net.SocketUtils
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.isActive
import java.io.File
import java.util.concurrent.TimeUnit

class HttpServer(private val options: Options) {

    data class Options(

        val port: Int,

        val tryOtherPorts: Boolean = true,

        val tryPortsCount: Int = 10,

        var customRoutings: List<RoutingDefinition> = listOf()
    )

    companion object {

        internal val threadPool = ThreadPoolUtils.newEagerThreadPool(
            5, 30, 60, TimeUnit.SECONDS
        )

        internal val coroutineDispatcher = threadPool.asCoroutineDispatcher()

        internal const val IMAGE_URL_PREFIX = "/android/img"

        internal val staticResourcesPrefixes = listOf(
            "/assets", "/font", "/img", "/js", "/favicon.ico"
        )
    }

    private var rawServer: EmbeddedServer<*, *>? = null

    var port: Int = 0
        private set

    val active: Boolean
        get() = rawServer?.application?.isActive == true

    @Synchronized
    fun start() {
        if(active) stop()
        port = options.run {
            if(tryOtherPorts) {
                SocketUtils.findAvailablePort(port, tryPortsCount)
            } else {
                port
            }
        }
        rawServer = embeddedServer(CIO, port, module = KtorModule.getModule(options))
        rawServer!!.start(false)
    }

    @Synchronized
    fun stop() {
        rawServer?.stop(timeoutMillis = 10 * 1000L)
    }
}

object DefaultHttpServer {

    private var serverOrNull: HttpServer? = null

    val server: HttpServer
        get() = serverOrNull!!

    val active: Boolean
        get() = serverOrNull?.active == true

    private val firstTryPortFile: File = run {
        File("${global.application.filesDir}/httpServer/port.txt").apply {
            if(exists()) return@apply
            FileUtil.touch(this)
            writeText("38081")
        }
    }

    private val firstTryPort: Int = firstTryPortFile.readText().toInt()

    var options: HttpServer.Options = HttpServer.Options(firstTryPort)

    @Synchronized
    fun start() {
        if(active) {
            server.stop()
        }
        serverOrNull = HttpServer(options)
        server.start()
        firstTryPortFile.writeText(server.port.toString())
    }

    @Synchronized
    fun stop() {
        serverOrNull?.stop()
        serverOrNull = null
    }

    @Synchronized
    fun restart() {
        stop()
        start()
    }

    @Synchronized
    fun restartIfStopped() {
        if(active) return
        restart()
    }

    fun getUrlByPath(path: String): String = "http://localhost:${server.port}$path"

    fun getImageUrlByPath(path: String): String = getUrlByPath("${HttpServer.IMAGE_URL_PREFIX}$path")

    fun getApiUrlByPath(path: String): String = getUrlByPath("/api$path")
}
