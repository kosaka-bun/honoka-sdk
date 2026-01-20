package de.honoka.sdk.util.kotlin.various

class RemoteInvokeException(

    override val message: String,

    val stackTraceText: String
) : RuntimeException(message)
