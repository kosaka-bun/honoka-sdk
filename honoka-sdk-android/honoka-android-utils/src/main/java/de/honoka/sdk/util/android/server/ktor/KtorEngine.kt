package de.honoka.sdk.util.android.server.ktor

import de.honoka.sdk.util.android.server.HttpServer
import de.honoka.sdk.util.android.server.RoutingDefinition
import de.honoka.sdk.util.kotlin.net.socket.SocketUtils
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import kotlinx.coroutines.isActive

class KtorEngine(private val options: Options = Options()) {

    data class Options(

        val firstTryPort: Int = HttpServer.Variables.FIRST_TRY_PORT,

        var customRoutings: List<RoutingDefinition> = listOf()
    )

    private var rawEngine: EmbeddedServer<*, *>? = null

    val isActive: Boolean
        get() = rawEngine?.application?.isActive == true

    var port: Int = 0
        private set

    fun start() {
        if(isActive) stop()
        port = SocketUtils.findAvailablePort(options.firstTryPort, 10)
        rawEngine = embeddedServer(CIO, port, module = KtorModule.getModule(options)).apply {
            start(false)
        }
    }

    fun stop() {
        rawEngine?.stop(timeoutMillis = 10 * 1000L)
    }
}
