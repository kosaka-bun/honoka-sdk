package de.honoka.sdk.util.kotlin.net.socket

import de.honoka.sdk.util.basic.javadoc.NotThreadSafe
import de.honoka.sdk.util.kotlin.basic.forEachCatching
import de.honoka.sdk.util.kotlin.basic.forEachInstant
import de.honoka.sdk.util.kotlin.basic.log
import java.io.Closeable
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.util.concurrent.ConcurrentHashMap

@NotThreadSafe
class StatusSelector(private val blocking: Boolean = false) : Closeable {
    
    private val selector: Selector = Selector.open()
    
    private val servers = ConcurrentHashMap<ServerSocketChannel, StatusSelectorEventCallback>()
    
    private val connections = ConcurrentHashMap<SocketChannel, SocketConnection>()
    
    @Volatile
    var closed = false
        private set
    
    fun register(channel: SocketChannel, fromChannel: ServerSocketChannel? = null): SocketConnection {
        if(closed) throw SelectorClosedException()
        val events = SelectionKey.OP_CONNECT or SelectionKey.OP_READ or SelectionKey.OP_WRITE
        channel.register(selector, events)
        val connection = SocketConnection(channel.remoteAddress.toString(), fromChannel, channel, selector)
        connections[channel] = connection
        return connection
    }
    
    fun registerServer(serverChannel: ServerSocketChannel, callback: StatusSelectorEventCallback) {
        if(closed) throw SelectorClosedException()
        serverChannel.register(selector, SelectionKey.OP_ACCEPT)
        servers[serverChannel] = callback
    }
    
    fun select() {
        if(closed) throw SelectorClosedException()
        removeClosedConnection()
        selector.run {
            if(blocking) {
                select()
            } else {
                selectNow()
            }
            selectedKeys().run {
                forEachCatching {
                    if(!it.isValid) {
                        connections[it.channel()]?.close()
                        return@forEachCatching
                    }
                    when {
                        it.isAcceptable -> onChannelAcceptable(it)
                        it.isConnectable -> onChannelConnectable(it)
                        it.isReadable -> onChannelReadable(it)
                        it.isWritable -> onChannelWritable(it)
                    }
                }
                clear()
            }
        }
    }
    
    private fun onChannelAcceptable(key: SelectionKey) {
        val serverChannel = key.channel() as ServerSocketChannel
        val connection = register(serverChannel.accept(), serverChannel)
        log.debug("Connection accepted: ${connection.channel}")
        runCatching {
            servers[serverChannel]!!.onAccpeted(connection)
        }.getOrElse {
            connection.close()
        }
    }
    
    private fun onChannelConnectable(key: SelectionKey) {
        connections[key.channel()]?.run {
            channel.finishConnect()
            log.debug("Connection established: $channel")
        }
    }
    
    private fun onChannelReadable(key: SelectionKey) {
        connections[key.channel()]?.run {
            readable = true
            log.debug("Connection readable: $channel")
            updateListeningEvents()
        }
    }
    
    private fun onChannelWritable(key: SelectionKey) {
        connections[key.channel()]?.run {
            writable = true
            log.debug("Connection writable: $channel")
            updateListeningEvents()
        }
    }
    
    private fun removeClosedConnection() {
        connections.forEachInstant { (k, v) ->
            v.runCatching {
                checkOrClose()
                if(closed) {
                    connections.remove(k)
                    runCatching {
                        fromChannel?.let {
                            servers[it]!!.onClosed(this)
                        }
                    }
                    log.debug("Connection $channel has been removed.")
                }
            }
        }
    }
    
    override fun close() {
        if(closed) return
        closed = true
        connections.forEachInstant {
            runCatching {
                it.value.close()
            }
        }
        selector.close()
    }
}

interface StatusSelectorEventCallback {
    
    fun onAccpeted(connection: SocketConnection)
    
    fun onClosed(connection: SocketConnection)
}

class SelectorClosedException(message: String? = null) : RuntimeException(message)
