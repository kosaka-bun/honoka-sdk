package de.honoka.sdk.util.kotlin.net.socket

import de.honoka.sdk.util.kotlin.basic.forEachCatching
import de.honoka.sdk.util.kotlin.basic.log
import java.io.Closeable
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.util.concurrent.ConcurrentHashMap

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
            removeClosedConnection()
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
        //由于connections是可并发读写的，因此遍历此集合前需要先创建其keys的副本
        connections.keys().toList().forEachCatching {
            connections[it]!!.run {
                checkOrClose()
                if(closed) {
                    connections.remove(it)
                    runCatching {
                        fromChannel?.let { c ->
                            servers[c]!!.onClosed(this)
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
        connections.forEachCatching { _, v ->
            v.close()
        }
        selector.close()
    }
}

interface StatusSelectorEventCallback {
    
    fun onAccpeted(connection: SocketConnection)
    
    fun onClosed(connection: SocketConnection)
}

class SelectorClosedException(message: String? = null) : RuntimeException(message)
