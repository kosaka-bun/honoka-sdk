package de.honoka.sdk.util.kotlin.net.socket

import de.honoka.sdk.util.basic.javadoc.ThreadSafe
import java.io.Closeable
import java.net.InetSocketAddress
import java.nio.channels.SocketChannel

@ThreadSafe
class NioSocketClient(private val blocking: Boolean = false) : Closeable {
    
    private val selector = StatusSelector(blocking)
    
    private val locks = mapOf(
        ::connect to Any(),
        ::refresh to Any()
    )
    
    fun connect(address: String): SocketConnection {
        if(selector.closed) throw SelectorClosedException()
        val connection = synchronized(locks[::connect]!!) {
            val addressPart = address.split(":")
            val channel = SocketChannel.open()
            channel.runCatching {
                configureBlocking(false)
                connect(InetSocketAddress(addressPart[0], addressPart[1].toInt()))
                selector.register(this)
            }.getOrElse {
                channel.close()
                throw it
            }
        }
        if(blocking) selector.wakeup()
        return connection
    }
    
    fun refresh() {
        synchronized(locks[::refresh]!!) {
            selector.select()
        }
    }

    override fun close() {
        selector.close()
    }
}
