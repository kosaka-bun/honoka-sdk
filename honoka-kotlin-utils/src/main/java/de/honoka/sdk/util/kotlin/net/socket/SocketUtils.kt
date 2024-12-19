package de.honoka.sdk.util.kotlin.net.socket

import de.honoka.sdk.util.kotlin.code.tryBlock
import java.net.BindException
import java.net.InetSocketAddress
import java.nio.channels.ServerSocketChannel

@Suppress("MemberVisibilityCanBePrivate")
object SocketUtils {
    
    fun newServerSocketChannel(firstTryPort: Int, tryCount: Int = 1): ServerSocketChannel {
        val channel = ServerSocketChannel.open()
        channel.run {
            configureBlocking(false)
            runCatching {
                tryBlock(tryCount, exceptionTypesToIgnore = listOf(BindException::class)) {
                    bind(InetSocketAddress(firstTryPort + it))
                }
            }.getOrElse {
                close()
                throw it
            }
        }
        return channel
    }
}