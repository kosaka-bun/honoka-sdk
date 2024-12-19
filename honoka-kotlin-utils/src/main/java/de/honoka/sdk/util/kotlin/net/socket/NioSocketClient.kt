package de.honoka.sdk.util.kotlin.net.socket

import java.io.Closeable
import java.net.InetSocketAddress
import java.nio.channels.SocketChannel

class NioSocketClient : Closeable {
    
    private val selector = StatusSelector()
    
    fun connect(address: String): SocketConnection {
        if(selector.closed) throw SelectorClosedException()
        val addressPart = address.split(":")
        val channel = SocketChannel.open()
        val connection = channel.runCatching {
            configureBlocking(false)
            connect(InetSocketAddress(addressPart[0], addressPart[1].toInt()))
            selector.register(this)
        }.getOrElse {
            channel.close()
            throw it
        }
        return connection
    }
    
    @Synchronized
    fun refresh() {
        selector.select()
    }

    @Synchronized
    override fun close() {
        selector.close()
    }
}
