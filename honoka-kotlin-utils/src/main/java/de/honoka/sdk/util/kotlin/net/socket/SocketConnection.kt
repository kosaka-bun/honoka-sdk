package de.honoka.sdk.util.kotlin.net.socket

import cn.hutool.core.date.DateTime
import cn.hutool.core.date.DateUnit
import de.honoka.sdk.util.basic.javadoc.NotThreadSafe
import de.honoka.sdk.util.basic.javadoc.ThreadSafe
import de.honoka.sdk.util.kotlin.basic.exception
import de.honoka.sdk.util.kotlin.basic.log
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

    @set:JvmSynthetic
    @Volatile
    var readable: Boolean = false
        internal set

    @set:JvmSynthetic
    @Volatile
    var writable: Boolean = false
        internal set

    @get:JvmSynthetic
    internal val writeBufferStream = ByteArrayOutputStream()
    
    @Volatile
    var lastReadOrWriteTime: DateTime = DateTime.now()
        private set
    
    @Volatile
    var closed: Boolean = false
        private set

    @JvmSynthetic
    internal fun updateListeningEvents(wakeupSelector: Boolean = false) {
        if(closed) exception("closed")
        val operations = run {
            var it = 0
            if(!readable) it += SelectionKey.OP_READ
            if(!writable) it += SelectionKey.OP_WRITE
            it
        }
        channel.register(selector, operations)
        if(wakeupSelector) selector.wakeup()
    }

    @JvmSynthetic
    internal fun read(bufferSize: Int = 10 * 1024): ByteArray {
        if(!readable || closed) exception("Not readable")
        val buffer = ByteBuffer.allocate(bufferSize)
        val readCount = runCatching {
            val c = channel.read(buffer)
            if(c < 0) exception("No bytes read.")
            readable = false
            updateListeningEvents(true)
            c
        }.getOrElse {
            close()
            throw it
        }
        log.debug("Read $readCount bytes from $address.")
        lastReadOrWriteTime = DateTime.now()
        return buffer.array().sliceArray(0 until readCount)
    }

    @JvmSynthetic
    internal fun write(bytes: ByteArray) {
        if(!writable || closed) exception("Not writable")
        runCatching {
            channel.write(ByteBuffer.wrap(bytes))
            writable = false
            updateListeningEvents(true)
        }.getOrElse {
            close()
            throw it
        }
        log.debug("Writed ${bytes.size} bytes to $address.")
        lastReadOrWriteTime = DateTime.now()
    }

    @ThreadSafe
    fun tryWrite(
        bytes: ByteArray,
        tryCount: Int = 1,
        waitTimeMillis: Long = 10L,
        throwOnFailed: Boolean = true
    ): Boolean {
        repeat(tryCount) {
            synchronized(this) {
                if(!writable) {
                    if(it + 1 < tryCount) {
                        Thread.sleep(waitTimeMillis)
                    }
                    return@repeat
                }
                write(bytes)
                return true
            }
        }
        if(throwOnFailed) {
            exception("Write timeout")
        } else {
            return false
        }
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
            SocketConnection(address=$address, fromServerChannel=${fromChannel != null}, |
            readable=$readable, writable=$writable, closed=$closed)
        """.singleLine()
    }
    
    override fun close() {
        if(closed) return
        closed = true
        runCatching {
            channel.close()
        }
        log.debug("Connection closed: {}", this)
        selector.wakeup()
    }
}
