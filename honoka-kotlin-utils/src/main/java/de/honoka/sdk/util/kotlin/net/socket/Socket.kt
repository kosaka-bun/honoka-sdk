package de.honoka.sdk.util.kotlin.net.socket

import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel

fun SocketChannel.register(selector: StatusSelector): SocketConnection = selector.register(this)

fun ServerSocketChannel.register(selector: StatusSelector, onAccepted: (SocketConnection) -> Unit) {
    selector.registerServer(this, onAccepted)
}
