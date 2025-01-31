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

abstract class NioSocketServer(protected val options: Options = Options()) : Closeable {

    data class Options(

        var firstTryPort: Int = 10000,

        var tryPortCount: Int = 20,

        var executorThreads: Int = 1,

        var bufferSize: Int = 10 * 2024
    )

    private val serverSocketChannel = SocketUtils.newServerSocketChannel(options.firstTryPort, options.tryPortCount)

    val port: Int
        get() = serverSocketChannel.localAddress.cast<InetSocketAddress>().port

    private val eventCallback: StatusSelectorEventCallback

    protected val selector = StatusSelector().apply {
        eventCallback = newEventCallback()
        registerServer(serverSocketChannel, eventCallback)
    }

    private val executor: ThreadPoolExecutor = run {
        Assert.isFalse(options.executorThreads < 1, "executorThreads < 1")
        Executors.newFixedThreadPool(options.executorThreads + 1) as ThreadPoolExecutor
    }

    init {
        startup()
    }

    protected open fun newEventCallback(): StatusSelectorEventCallback = EmptyStatusSelectorEventCallback

    private fun startup() {
        startSelectorTask()
    }

    private fun startSelectorTask() {
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
                if(!it.needReport) return@forEach
                executor.submit {
                    if(Thread.currentThread().isInterrupted) return@submit
                    synchronized(it) {
                        it.checkOrClose()
                        if(it.needReportReadable) {
                            runCatching {
                                onReadable(it)
                            }.getOrElse { e ->
                                if(e is SocketReadEofException) return@getOrElse
                                log.error("", e)
                            }
                        }
                        if(it.needReportWritable) {
                            runCatching {
                                onWritable(it)
                            }.getOrElse { e ->
                                log.error("", e)
                            }
                        }
                    }
                }
            }
        }
    }

    abstract fun onReadable(connection: SocketConnection)

    open fun onWritable(connection: SocketConnection) {
        connection.writableReported = true
    }

    protected open fun closeExecutor() {
        executor.shutdownNowAndWait()
    }

    @Synchronized
    override fun close() {
        closeExecutor()
        selector.close()
        serverSocketChannel.close()
    }
}
