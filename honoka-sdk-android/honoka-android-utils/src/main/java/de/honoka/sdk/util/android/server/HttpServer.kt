package de.honoka.sdk.util.android.server

import de.honoka.sdk.util.android.server.ktor.KtorModule
import de.honoka.sdk.util.kotlin.net.SocketUtils
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import kotlinx.coroutines.isActive

class HttpServer(private val options: Options) {

    data class Options(

        val port: Int,

        val tryOtherPorts: Boolean = true,

        val tryPortsCount: Int = 10,

        var customRoutings: List<RoutingDefinition> = listOf()
    )

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
