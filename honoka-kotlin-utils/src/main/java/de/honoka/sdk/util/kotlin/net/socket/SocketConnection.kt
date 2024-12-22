package de.honoka.sdk.util.kotlin.net.socket

import cn.hutool.core.date.DateTime
import cn.hutool.core.date.DateUnit
import de.honoka.sdk.util.basic.javadoc.NotThreadSafe
import de.honoka.sdk.util.kotlin.basic.exception
import de.honoka.sdk.util.kotlin.text.singleLine
import java.io.ByteArrayOutputStream
import java.io.Closeable
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel

@NotThreadSafe
@Suppress("MemberVisibilityCanBePrivate")
class SocketConnection(
    val address: String,
    val fromChannel: ServerSocketChannel? = null,
    val channel: SocketChannel,
    val selector: Selector
) : Closeable {
    
    @Volatile
    var readable: Boolean = false
        internal set
    
    @Volatile
    var writable: Boolean = false
        internal set
    
    val writeBufferStream = ByteArrayOutputStream()
    
    @Volatile
    var lastReadOrWriteTime: DateTime = DateTime.now()
        private set
    
    @Volatile
    var closed: Boolean = false
        private set
    
    fun updateListeningEvents() {
        if(closed) exception("closed")
        val operations = run {
            var it = 0
            if(!readable) it += SelectionKey.OP_READ
            if(!writable) it += SelectionKey.OP_WRITE
            it
        }
        channel.register(selector, operations)
    }
    
    fun read(bufferSize: Int = 10 * 1024): ByteArray {
        if(!readable || closed) exception("Not readable")
        val buffer = ByteBuffer.allocate(bufferSize)
        val readCount = runCatching {
            val c = channel.read(buffer)
            readable = false
            updateListeningEvents()
            c
        }.getOrElse {
            close()
            throw it
        }
        lastReadOrWriteTime = DateTime.now()
        return buffer.array().sliceArray(0 until readCount)
    }
    
    fun write(bytes: ByteArray) {
        if(!writable || closed) exception("Not writable")
        runCatching {
            channel.write(ByteBuffer.wrap(bytes))
            writable = false
            updateListeningEvents()
        }.getOrElse {
            close()
            throw it
        }
        lastReadOrWriteTime = DateTime.now()
    }
    
    fun checkOrClose() {
        if(closed) return
        channel.run {
            val isValid = isOpen && (isConnected || isConnectionPending) && run {
                DateTime.now().between(lastReadOrWriteTime, DateUnit.SECOND) < 180
            }
            if(isValid) return
            close()
        }
    }
    
    override fun toString(): String = run {
        """
            SocketConnection(address=$address, channel=$channel, |
            selector=$selector, readable=$readable, |
            writable=$writable, closed=$closed)
        """.singleLine()
    }
    
    override fun close() {
        if(closed) return
        closed = true
        channel.close()
    }
}
