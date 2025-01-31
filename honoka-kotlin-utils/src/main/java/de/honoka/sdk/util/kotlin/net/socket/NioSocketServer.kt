package de.honoka.sdk.util.kotlin.net.socket

import cn.hutool.core.lang.Assert
import de.honoka.sdk.util.kotlin.basic.cast
import de.honoka.sdk.util.kotlin.basic.isAnyType
import de.honoka.sdk.util.kotlin.basic.log
import de.honoka.sdk.util.kotlin.concurrent.shutdownNowAndWait
import de.honoka.sdk.util.kotlin.net.socket.selector.EmptyStatusSelectorEventCallback
import de.honoka.sdk.util.kotlin.net.socket.selector.SelectorClosedException
import de.honoka.sdk.util.kotlin.net.socket.selector.StatusSelector
import de.honoka.sdk.util.kotlin.net.socket.selector.StatusSelectorEventCallback
import java.io.Closeable
import java.net.InetSocketAddress
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor

abstract class NioSocketServer(private val options: Options = Options()) : Closeable {

    data class Options(

        var firstTryPort: Int = 10000,

        var tryPortCount: Int = 20,

        var executorThreads: Int = 1,

        var bufferSize: Int = 10 * 2024
    )

    private val serverSocketChannel = SocketUtils.newServerSocketChannel(options.firstTryPort, options.tryPortCount)

    val port: Int
        get() = serverSocketChannel.localAddress.cast<InetSocketAddress>().port

    protected open val eventCallback: StatusSelectorEventCallback = EmptyStatusSelectorEventCallback

    private val selector = StatusSelector().apply {
        registerServer(serverSocketChannel, eventCallback)
    }

    private val executor: ThreadPoolExecutor = run {
        Assert.isFalse(options.executorThreads < 1, "executorThreads < 1")
        Executors.newFixedThreadPool(options.executorThreads + 1) as ThreadPoolExecutor
    }

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

    private fun handleConnections() {
        selector.run {
            select()
            connectionsView.forEach {
                if(!it.readable) return@forEach
                executor.submit {
                    synchronized(it) {
                        if(!it.readable) return@submit
                        runCatching {
                            val bytes = runCatching {
                                it.read()
                            }.getOrElse { _ ->
                                return@runCatching
                            }
                            onReadable(it, bytes)
                        }.getOrElse { e ->
                            log.error("", e)
                        }
                    }
                }
            }
        }
    }

    abstract fun onReadable(connection: SocketConnection, bytes: ByteArray)

    @Synchronized
    override fun close() {
        executor.shutdownNowAndWait()
        selector.close()
        serverSocketChannel.close()
    }
}
