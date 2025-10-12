package de.honoka.sdk.util.android.server

import de.honoka.sdk.util.android.server.ktor.KtorEngine
import de.honoka.sdk.util.concurrent.ThreadPoolUtils
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.TimeUnit

object HttpServer {

    object Variables {

        internal const val FIRST_TRY_PORT = 38081

        internal const val IMAGE_URL_PREFIX = "/android/img"

        fun getUrlByPath(path: String): String = "http://localhost:${server!!.port}$path"

        fun getImageUrlByPath(path: String): String = getUrlByPath("$IMAGE_URL_PREFIX$path")

        fun getApiUrlByPath(path: String): String = getUrlByPath("/api$path")
    }

    internal var server: KtorEngine? = null

    val isActive: Boolean
        get() = server?.isActive == true

    internal val threadPool = ThreadPoolUtils.newEagerThreadPool(
        5, 30, 60, TimeUnit.SECONDS
    )

    internal val coroutineDispatcher = threadPool.asCoroutineDispatcher()

    internal val staticResourcesPrefixes = arrayOf(
        "/assets", "/font", "/img", "/js", "/favicon.ico"
    )

    private var usingOptions: KtorEngine.Options = KtorEngine.Options()

    @Synchronized
    fun start(options: KtorEngine.Options? = null): KtorEngine {
        if(isActive) {
            server!!.stop()
        }
        options?.let {
            usingOptions = it
        }
        server = KtorEngine(usingOptions).apply {
            start()
        }
        return server!!
    }

    @Synchronized
    fun restartIfStopped(): KtorEngine = if(isActive) server!! else start()
}
