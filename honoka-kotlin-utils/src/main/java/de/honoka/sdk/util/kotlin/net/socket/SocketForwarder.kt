package de.honoka.sdk.util.kotlin.net.socket

import de.honoka.sdk.util.kotlin.basic.*
import java.io.Closeable
import java.net.InetSocketAddress
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

class SocketForwarder(
    private val targets: Set<String>,
    private val options: Options = Options()
) : Closeable {
    
    data class Options(
        
        var firstTryPort: Int = 10000,
        
        var tryPortCount: Int = 20,
        
        var executorThreads: Int = 1,
    
        var bufferSize: Int = 10 * 2024
    )
    
    private inner class SelectorCallback : StatusSelectorEventCallback {
        
        override fun onAccpeted(connection: SocketConnection) {
            val target = tryBlockNullable(3) { targets.randomOrNull() }
            target ?: exception("No avaliable target.")
            val targetConnection = nioSocketClient.connect(target)
            connectionMap[connection] = targetConnection
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
    
    private val selector = StatusSelector(true).apply {
        registerServer(serverSocketChannel, SelectorCallback())
    }
    
    private val nioSocketClient = NioSocketClient()
    
    private val connectionMap = ConcurrentHashMap<SocketConnection, SocketConnection>()
    
    private val executor = Executors.newFixedThreadPool(options.executorThreads + 1)
    
    init {
        startup()
    }
    
    private fun startup() {
        executor.submit {
            while(true) {
                if(Thread.currentThread().isInterrupted) break
                try {
                    handleConnections()
                } catch(t: Throwable) {
                    val typesToThrow = listOf(SelectorClosedException::class)
                    if(t.isAnyType(typesToThrow)) throw t
                }
            }
        }
    }
    
    @Synchronized
    private fun handleConnections() {
        selector.select()
        nioSocketClient.refresh()
        connectionMap.forEachKv { k, v ->
            executor.submit {
                if(Thread.currentThread().isInterrupted) return@submit
                runCatching {
                    forward(k, v)
                    forward(v, k)
                }
            }
        }
    }
    
    private fun forward(from: SocketConnection, to: SocketConnection) {
        if(!from.readable || !to.writable) return
        to.write(from.read(options.bufferSize))
    }
    
    @Synchronized
    override fun close() {
        executor.shutdownNowAndWait()
        nioSocketClient.close()
        selector.close()
        serverSocketChannel.close()
    }
}
