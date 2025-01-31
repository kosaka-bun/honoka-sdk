package de.honoka.sdk.util.kotlin.net.socket

import de.honoka.sdk.util.basic.javadoc.ThreadSafe
import de.honoka.sdk.util.kotlin.basic.exception
import de.honoka.sdk.util.kotlin.basic.forEachInstant
import de.honoka.sdk.util.kotlin.basic.isAnyType
import de.honoka.sdk.util.kotlin.basic.tryBlockNullable
import de.honoka.sdk.util.kotlin.concurrent.shutdownNowAndWait
import de.honoka.sdk.util.kotlin.net.socket.selector.SelectorClosedException
import de.honoka.sdk.util.kotlin.net.socket.selector.StatusSelectorEventCallback
import java.io.Closeable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

class SocketForwarder(
    private val targets: Set<String>,
    options: Options = Options()
) : NioSocketServer(options), Closeable {
    
    private val nioSocketClient = NioSocketClient()
    
    private val connectionMap = ConcurrentHashMap<SocketConnection, SocketConnection>()
    
    private val executor = Executors.newFixedThreadPool(1)
    
    init {
        startup()
    }

    override fun newEventCallback(): StatusSelectorEventCallback = @ThreadSafe object : StatusSelectorEventCallback {

            override fun onAccpeted(connection: SocketConnection) {
                val target = tryBlockNullable(3) { targets.randomOrNull() }
                target ?: exception("No avaliable target.")
                val targetConnection = nioSocketClient.connect(target)
                connectionMap[connection] = targetConnection
                selector.wakeup()
            }

            override fun onClosed(connection: SocketConnection) {
                val targetConnection = connectionMap[connection]
                connectionMap.remove(targetConnection)
                runCatching {
                    targetConnection?.close()
                }
            }
        }

    private fun startup() {
        startClientSelectorTask()
    }
    
    private fun startClientSelectorTask() {
        executor.submit {
            while(true) {
                if(Thread.currentThread().isInterrupted) break
                try {
                    nioSocketClient.refresh()
                    selector.wakeup()
                } catch(t: Throwable) {
                    val typesToThrow = listOf(SelectorClosedException::class)
                    if(t.isAnyType(typesToThrow)) throw t
                }
            }
        }
    }

    override fun onReadable(connection: SocketConnection) {
        val clientConnection = connectionMap[connection] ?: return
        synchronized(clientConnection) {
            forwardBidirectionally(connection, clientConnection)
        }
    }

    override fun onWritable(connection: SocketConnection) {
        val clientConnection = connectionMap[connection] ?: return
        synchronized(clientConnection) {
            forwardBidirectionally(clientConnection, connection)
        }
    }

    private fun forward(from: SocketConnection, to: SocketConnection) {
        if(from.readable) {
            to.writeBufferStream.write(from.read(options.bufferSize))
        }
        if(to.writable && to.writeBufferStream.size() > 0) {
            val bytes = to.writeBufferStream.run {
                toByteArray().also { reset() }
            }
            to.write(bytes)
        }
    }

    private fun forwardBidirectionally(connection1: SocketConnection, connection2: SocketConnection) {
        runCatching {
            forward(connection1, connection2)
            forward(connection2, connection1)
        }.getOrElse {
            connection1.close()
            connection2.close()
        }
    }

    fun closeAllConnections() {
        connectionMap.forEachInstant { (k, v) ->
            runCatching {
                k.close()
                v.close()
            }
        }
    }

    override fun closeExecutor() {
        super.closeExecutor()
        executor.shutdownNowAndWait()
    }

    @Synchronized
    override fun close() {
        closeExecutor()
        nioSocketClient.close()
        super.close()
    }
}
