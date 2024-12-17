package de.honoka.sdk.util.kotlin.net.socket

import de.honoka.sdk.util.kotlin.code.exception
import de.honoka.sdk.util.kotlin.code.log
import de.honoka.sdk.util.kotlin.code.tryBlockNullable
import java.io.Closeable
import java.net.BindException
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

@Suppress("MemberVisibilityCanBePrivate", "LoggingStringTemplateAsArgument")
class SocketForwarder(private val targets: Set<String>, executorThreads: Int = 1) : Closeable {
    
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
                log.debug("Forward ended: Client = $socket")
            }.getOrElse {
                log.debug("Exception when handling connection: $socket", it)
            }
        }
    }
    
    private fun handleConnection(socket: Socket) {
        val target = tryBlockNullable(3) { targets.randomOrNull() }?.split(":")
        target ?: exception("No avaliable target.")
        val targetSocket = Socket(target[0], target[1].toInt())
        targetSocket.use {
            socket.soTimeout = 20
            it.soTimeout = 20
            /*
             * 空转发次数：如果一次转发过程（先客户端到服务端，再服务端到客户端）中的两次转发行为均没有
             * 转发任何数据（转发字节数为0），则认为这次转发是一次空转发。
             */
            var emptyForwardTimes = 0
            /*
             * 由于SocketForwarder与客户端和服务端之间各自建立的Socket连接均设置了读取超时时间为20ms，
             * 因此在Socket连接对应的输入流中没有数据可读时，read方法最多阻塞20ms，因此一次转发过程在
             * 数据转发完成后，最多阻塞40ms。
             *
             * 设置空转发连续次数不超过50次，可保证在2秒内，转发行为会每20ms尝试一次。
             * 若2秒内均不能进行有效转发，则终止转发。
             */
            while(emptyForwardTimes < 50) {
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
            val buffer = ByteArray(10 * 1024)
            val readBytesCount = runCatching {
                from.getInputStream().read(buffer)
            }.getOrDefault(0)
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
