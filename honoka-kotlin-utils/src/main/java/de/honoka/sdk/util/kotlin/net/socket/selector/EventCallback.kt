package de.honoka.sdk.util.kotlin.net.socket.selector

import de.honoka.sdk.util.kotlin.net.socket.SocketConnection

interface StatusSelectorEventCallback {

    fun onAccpeted(connection: SocketConnection)

    fun onClosed(connection: SocketConnection)
}

object EmptyStatusSelectorEventCallback : StatusSelectorEventCallback {

    override fun onAccpeted(connection: SocketConnection) {}

    override fun onClosed(connection: SocketConnection) {}
}
