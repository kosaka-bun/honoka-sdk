package de.honoka.sdk.util.kotlin.net.socket

import de.honoka.sdk.util.kotlin.code.exception
import de.honoka.sdk.util.kotlin.code.forEachCatching
import de.honoka.sdk.util.kotlin.code.log
import java.io.Closeable
import java.net.InetSocketAddress
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.SocketChannel
import java.util.concurrent.ConcurrentHashMap

class SocketConnectionContainer : Closeable {
    
    private val selector: Selector = Selector.open()
    
    private val connections = ConcurrentHashMap<SocketChannel, SocketConnection>()
    
    @Volatile
    private var closed = false
    
    fun connect(address: String): SocketConnection {
        if(closed) exception("closed")
        val channel = SocketChannel.open().apply {
            val addressPart = address.split(":")
            configureBlocking(false)
            connect(InetSocketAddress(addressPart[0], addressPart[1].toInt()))
            val events = SelectionKey.OP_CONNECT or SelectionKey.OP_READ or SelectionKey.OP_WRITE
            register(selector, events)
        }
        val connection = SocketConnection(address, channel, selector)
        connections[channel] = connection
        return connection
    }
    
    @Synchronized
    fun refresh() {
        if(closed) exception("closed")
        selector.run {
            selectNow()
            selectedKeys().run {
                forEachCatching {
                    when {
                        it.isConnectable -> onChannelConnectable(it)
                        it.isReadable -> onChannelReadable(it)
                        it.isWritable -> onChannelWritable(it)
                    }
                }
                clear()
            }
            removeClosedConnection()
        }
    }
    
    private fun onChannelConnectable(key: SelectionKey) {
        connections[key.channel()]?.run {
            channel?.finishConnect()
            log.debug("Connection established: $channel")
        }
    }
    
    private fun onChannelReadable(key: SelectionKey) {
        connections[key.channel()]?.run {
            readable = true
            log.debug("Connection readable: $channel")
            register()
        }
    }
    
    private fun onChannelWritable(key: SelectionKey) {
        connections[key.channel()]?.run {
            writable = true
            log.debug("Connection writable: $channel")
            register()
        }
    }
    
    private fun removeClosedConnection() {
        connections.keys().toList().forEachCatching {
            val connection = connections[it]!!
            if(connection.closed) {
                connections.remove(it)
                log.debug("Connection ${connection.channel} has been removed.")
                connection.close()
            }
        }
    }
    
    @Synchronized
    override fun close() {
        if(!closed) closed = true
        connections.entries.forEachCatching {
            it.value.close()
        }
        selector.close()
    }
}
