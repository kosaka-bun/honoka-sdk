package de.honoka.sdk.util.android.server

import android.content.Intent
import cn.hutool.core.io.FileUtil
import de.honoka.sdk.util.android.basic.global
import de.honoka.sdk.util.android.service.SingletonService
import de.honoka.sdk.util.concurrent.ThreadPoolUtils
import kotlinx.coroutines.asCoroutineDispatcher
import java.io.File
import java.util.concurrent.TimeUnit

class HttpServerService : SingletonService() {

    companion object : AbstractCompanion<HttpServerService>(HttpServerService::class) {

        internal const val IMAGE_URL_PREFIX = "/android/img"

        internal val staticResourcesPrefixes = arrayOf(
            "/assets", "/font", "/img", "/js", "/favicon.ico"
        )

        internal val firstTryPortFile: File = run {
            File("${global.application.filesDir}/httpServer/port.txt").apply {
                if(exists()) return@apply
                FileUtil.touch(this)
                writeText("38081")
            }
        }

        internal val firstTryPort: Int = firstTryPortFile.readText().toInt()

        var options: HttpServer.Options = HttpServer.Options(firstTryPort)

        internal val threadPool = ThreadPoolUtils.newEagerThreadPool(
            5, 30, 60, TimeUnit.SECONDS
        )

        internal val coroutineDispatcher = threadPool.asCoroutineDispatcher()

        fun getUrlByPath(path: String): String = "http://localhost:${instance!!.server.port}$path"

        fun getImageUrlByPath(path: String): String = getUrlByPath("$IMAGE_URL_PREFIX$path")

        fun getApiUrlByPath(path: String): String = getUrlByPath("/api$path")
    }

    override val companion: AbstractCompanion<out SingletonService> = Companion

    var serverOrNull: HttpServer? = null
        private set

    val server: HttpServer
        get() = serverOrNull!!

    override val active: Boolean
        get() = serverOrNull?.active == true

    override fun onStartCommandExt(intent: Intent?, flags: Int, startId: Int) {
        if(active) {
            server.stop()
        }
        serverOrNull = HttpServer(options)
        server.start()
        firstTryPortFile.writeText(server.port.toString())
    }

    override fun onDestroyExt() {
        serverOrNull?.stop()
        serverOrNull = null
    }
}
