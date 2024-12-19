package de.honoka.sdk.util.kotlin.net.socket

import de.honoka.sdk.util.kotlin.code.exception
import de.honoka.sdk.util.kotlin.code.forEachCatching
import de.honoka.sdk.util.kotlin.code.log
import java.io.Closeable
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.util.concurrent.ConcurrentHashMap

class StatusSelector(private val blocking: Boolean = false) : Closeable {
    
    private val selector: Selector = Selector.open()
    
    private val servers = ConcurrentHashMap<ServerSocketChannel, (SocketConnection) -> Unit>()
    
    private val connections = ConcurrentHashMap<SocketChannel, SocketConnection>()
    
    @Volatile
    private var closed = false
    
    fun register(channel: SocketChannel): SocketConnection {
        if(closed) exception("closed")
        val events = SelectionKey.OP_CONNECT or SelectionKey.OP_READ or SelectionKey.OP_WRITE
        channel.register(selector, events)
        val connection = SocketConnection(channel.remoteAddress.toString(), channel, selector)
        connections[channel] = connection
        return connection
    }
    
    fun registerServer(serverChannel: ServerSocketChannel, onAccepted: (SocketConnection) -> Unit) {
        if(closed) exception("closed")
        serverChannel.register(selector, SelectionKey.OP_ACCEPT)
        servers[serverChannel] = onAccepted
    }
    
    fun select() {
        if(closed) exception("closed")
        selector.run {
            if(blocking) {
                select()
            } else {
                selectNow()
            }
            selectedKeys().run {
                forEachCatching {
                    if(!it.isValid) return@forEachCatching
                    when {
                        it.isAcceptable -> onChannelAcceptable(it)
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
    
    private fun onChannelAcceptable(key: SelectionKey) {
        val serverChannel = key.channel() as ServerSocketChannel
        val connection = register(serverChannel.accept())
        log.debug("Connection accepted: ${connection.channel}")
        servers[serverChannel]!!(connection)
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
            }
        }
    }
    
    override fun close() {
        if(closed) return
        closed = true
        connections.entries.forEachCatching {
            it.value.close()
        }
        selector.close()
    }
}
