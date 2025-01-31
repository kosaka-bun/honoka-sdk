package de.honoka.sdk.util.kotlin.net.socket

import de.honoka.sdk.util.basic.javadoc.ThreadSafe
import de.honoka.sdk.util.kotlin.basic.log
import de.honoka.sdk.util.kotlin.net.socket.selector.SelectorClosedException
import de.honoka.sdk.util.kotlin.net.socket.selector.StatusSelector
import java.io.Closeable
import java.net.InetSocketAddress
import java.nio.channels.SocketChannel

@ThreadSafe
class NioSocketClient : Closeable {
    
    private val selector = StatusSelector()
    
    private val locks = mapOf(::connect to Any(), ::refresh to Any())
    
    fun connect(address: String): SocketConnection {
        if(selector.closed) throw SelectorClosedException()
        val connection = synchronized(locks[::connect]!!) {
            val addressPart = address.split(":")
            val channel = SocketChannel.open()
            channel.runCatching {
                connect(InetSocketAddress(addressPart[0], addressPart[1].toInt()))
                configureBlocking(false)
                selector.register(this)
            }.getOrElse {
                channel.close()
                throw it
            }
        }
        log.debug("Connection established: $connection")
        selector.wakeup()
        return connection
    }
    
    fun refresh(blocking: Boolean = true) {
        synchronized(locks[::refresh]!!) {
            selector.select(blocking)
        }
    }

    override fun close() {
        selector.close()
    }
}
