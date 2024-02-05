package de.honoka.sdk.util.android.jsinterface.async

data class AsyncTaskResult(

    @JvmField
    var isResolve: Boolean? = null,

    @JvmField
    var isPlainText: Boolean? = null,

    var message: String? = null,

    var result: Any? = null
)