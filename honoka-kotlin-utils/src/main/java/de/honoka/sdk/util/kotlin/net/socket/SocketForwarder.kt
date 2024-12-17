package de.honoka.sdk.util.kotlin.net.socket

import de.honoka.sdk.util.kotlin.code.exception
import de.honoka.sdk.util.kotlin.code.isSubClassOf
import de.honoka.sdk.util.kotlin.code.log
import de.honoka.sdk.util.kotlin.code.tryBlockNullable
import java.io.Closeable
import java.net.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

@Suppress("MemberVisibilityCanBePrivate", "LoggingStringTemplateAsArgument")
class SocketForwarder(
    private val targets: Set<String>,
    executorThreads: Int = 1,
    /**
     * 从客户端或服务端读取数据但暂无数据可读时的最大等待时长（即[Socket.getSoTimeout]）。
     *
     * 由于采用了同一个线程先后从客户端或服务端读取数据，因此若此参数过大，可能会导致线程在某个转发
     * 方向上的等待时间过长，从而导致另一个方向的数据转发产生较大延迟。
     */
    private val socketReadTimeout: Int = 20,
    /**
     * 持续出现空转发情况时，在主动关闭与客户端和与服务端之间的两个Socket连接前所等待的最大时长。
     *
     * 若此参数过大，可能会导致[executor]中的线程被长时间无效占用。
     *
     * @see handleConnection
     */
    private val timeoutOnEmptyForward: Int = 60 * 1000,
    private val bufferSize: Int = 10 * 2024
) : Closeable {
    
    var port: Int = 0
        private set
    
    private val serverSocket: ServerSocket = run {
        var t: Throwable? = null
        repeat(20) {
            port = 10000 + it
            try {
                return@run ServerSocket(port)
            } catch(e: BindException) {
                t = e
            }
        }
        throw t!!
    }
    
    private val executor = ThreadPoolExecutor(
        executorThreads + 1, executorThreads + 1,
        0, TimeUnit.SECONDS,
        LinkedBlockingQueue()
    )
    
    init {
        startup()
    }
    
    private fun startup() {
        executor.submit {
            while(true) {
                runCatching {
                    if(Thread.currentThread().isInterrupted) return@submit
                    accept()
                }
            }
        }
    }
    
    private fun accept() {
        val socket = serverSocket.accept()
        log.debug("Accepted socket: $socket")
        executor.submit {
            runCatching {
                socket.use { handleConnection(it) }
            }.getOrElse { e ->
                listOf(
                    SocketException::class
                ).forEach {
                    if(e::class.isSubClassOf(it)) return@getOrElse
                }
                log.debug("Exception when handling connection: $socket", e)
                return@submit
            }
            log.debug("Forward ended: Client = $socket")
        }
    }
    
    private fun handleConnection(socket: Socket) {
        val target = tryBlockNullable(3) { targets.randomOrNull() }?.split(":")
        target ?: exception("No avaliable target.")
        val targetSocket = Socket(target[0], target[1].toInt())
        targetSocket.use {
            socket.soTimeout = socketReadTimeout
            it.soTimeout = socketReadTimeout
            /*
             * 空转发次数：如果一次转发过程（先客户端到服务端，再服务端到客户端）中的两次转发行为均没有
             * 转发任何数据（转发字节数为0），则认为这次转发是一次空转发。
             */
            var emptyForwardTimes = 0
            /*
             * 由于SocketForwarder与客户端和服务端之间各自建立的Socket连接均设置了读取超时时间（默认
             * 为20ms），因此在Socket连接对应的输入流中没有数据可读时，read方法最多阻塞20ms，因此一次
             * 转发过程在数据转发完成后，最多阻塞40ms。
             *
             * 在一段时间内，转发行为会每20ms尝试一次。设置最大空转发连续次数，可保证若在一段时间（默认
             * 为60秒）内均不能进行任何有效的转发，则终止转发。
             *
             * 最大空转发连续次数 = 连续空转发等待时长 ÷ (Socket连接读取超时 × 2)
             */
            while(emptyForwardTimes < timeoutOnEmptyForward / (socketReadTimeout * 2)) {
                var totalForwardedCount = 0
                totalForwardedCount += forward(socket, it)
                totalForwardedCount += forward(it, socket)
                if(totalForwardedCount < 1) {
                    emptyForwardTimes++
                } else {
                    emptyForwardTimes = 0
                }
            }
        }
    }
    
    private fun forward(from: Socket, to: Socket): Int {
        var totalReadCount = 0
        while(true) {
            val buffer = ByteArray(bufferSize)
            val readBytesCount = try {
                from.getInputStream().read(buffer)
            } catch(e: SocketTimeoutException) {
                0
            }
            if(readBytesCount < 1) break
            totalReadCount += readBytesCount
            to.getOutputStream().write(buffer, 0, readBytesCount)
        }
        return totalReadCount
    }
    
    override fun close() {
        serverSocket.close()
        executor.shutdownNow()
    }
}
