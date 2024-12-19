package de.honoka.sdk.util.kotlin.net.socket

import de.honoka.sdk.util.kotlin.code.exception
import java.io.Closeable
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.SocketChannel

data class SocketConnection(
    
    var address: String? = null,
    
    var channel: SocketChannel? = null,
    
    var selector: Selector? = null,
    
    @Volatile
    var readable: Boolean = false,
    
    @Volatile
    var writable: Boolean = false,
    
    @Volatile
    var closed: Boolean = false
) : Closeable {
    
    fun register() {
        val operations = run {
            var it = 0
            if(!readable) it += SelectionKey.OP_READ
            if(!writable) it += SelectionKey.OP_WRITE
            it
        }
        channel?.register(selector, operations)
    }
    
    fun read(bufferSize: Int = 10 * 1024): ByteArray {
        if(!readable || closed) exception("Not readable")
        val buffer = ByteBuffer.allocate(bufferSize)
        val readCount = runCatching {
            val c = channel!!.read(buffer)
            readable = false
            register()
            c
        }.getOrElse {
            closed = true
            throw it
        }
        return buffer.array().sliceArray(0 until readCount)
    }
    
    fun write(bytes: ByteArray) {
        if(!writable || closed) exception("Not writable")
        runCatching {
            channel!!.write(ByteBuffer.wrap(bytes))
            writable = false
            register()
        }.getOrElse {
            closed = true
            throw it
        }
    }
    
    override fun close() {
        closed = true
        channel?.close()
    }
}
