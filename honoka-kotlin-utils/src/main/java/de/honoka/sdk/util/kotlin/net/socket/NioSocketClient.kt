package de.honoka.sdk.util.kotlin.net.socket

import de.honoka.sdk.util.kotlin.code.exception
import java.io.Closeable
import java.net.InetSocketAddress
import java.nio.channels.SocketChannel

class NioSocketClient : Closeable {
    
    private val selector = StatusSelector()
    
    @Volatile
    private var closed = false
    
    fun connect(address: String): SocketConnection {
        if(closed) exception("closed")
        val addressPart = address.split(":")
        val channel = SocketChannel.open()
        val connection = channel.runCatching {
            configureBlocking(false)
            connect(InetSocketAddress(addressPart[0], addressPart[1].toInt()))
            register(selector)
        }.getOrElse {
            channel.close()
            throw it
        }
        return connection
    }
    
    @Synchronized
    fun refresh() {
        if(closed) exception("closed")
        selector.select()
    }

    @Synchronized
    override fun close() {
        if(closed) return
        closed = true
        selector.close()
    }
}
