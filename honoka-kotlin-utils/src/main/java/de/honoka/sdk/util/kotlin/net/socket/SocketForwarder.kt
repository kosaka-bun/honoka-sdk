package de.honoka.sdk.util.kotlin.net.socket

import de.honoka.sdk.util.basic.javadoc.ThreadSafe
import de.honoka.sdk.util.kotlin.basic.*
import de.honoka.sdk.util.kotlin.concurrent.doubleSynchronized
import de.honoka.sdk.util.kotlin.concurrent.shutdownNowAndWait
import java.io.Closeable
import java.net.InetSocketAddress
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

class SocketForwarder(private val targets: Set<String>, private val options: Options = Options()) : Closeable {
    
    data class Options(
        
        var firstTryPort: Int = 10000,
        
        var tryPortCount: Int = 20,
        
        var executorThreads: Int = 1,
    
        var bufferSize: Int = 10 * 2024
    )
    
    @ThreadSafe
    private inner class SelectorCallback : StatusSelectorEventCallback {
        
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

    private val serverSocketChannel = SocketUtils.newServerSocketChannel(options.firstTryPort, options.tryPortCount)
    
    val port: Int
        get() = serverSocketChannel.localAddress.cast<InetSocketAddress>().port
    
    private val selector = StatusSelector().apply {
        registerServer(serverSocketChannel, SelectorCallback())
    }
    
    private val nioSocketClient = NioSocketClient()
    
    private val connectionMap = ConcurrentHashMap<SocketConnection, SocketConnection>()
    
    private val executor = Executors.newFixedThreadPool(options.executorThreads + 2)
    
    init {
        startup()
    }
    
    private fun startup() {
        submitSelectorTask {
            handleConnections()
        }
        submitSelectorTask {
            nioSocketClient.refresh()
            selector.wakeup()
        }
    }
    
    private inline fun submitSelectorTask(crossinline block: () -> Unit) {
        executor.submit {
            while(true) {
                if(Thread.currentThread().isInterrupted) break
                try {
                    block()
                } catch(t: Throwable) {
                    val typesToThrow = listOf(SelectorClosedException::class)
                    if(t.isAnyType(typesToThrow)) throw t
                }
            }
        }
    }
    
    private fun handleConnections() {
        selector.select()
        connectionMap.forEach { (k, v) ->
            executor.submit {
                if(Thread.currentThread().isInterrupted) return@submit
                doubleSynchronized(k, v) {
                    runCatching {
                        forward(k, v)
                        forward(v, k)
                    }.getOrElse {
                        k.close()
                        v.close()
                    }
                }
            }
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
    
    fun closeAllConnections() {
        connectionMap.forEachInstant { (k, v) ->
            runCatching {
                k.close()
                v.close()
            }
        }
    }
    
    @Synchronized
    override fun close() {
        executor.shutdownNowAndWait()
        nioSocketClient.close()
        selector.close()
        serverSocketChannel.close()
    }
}
