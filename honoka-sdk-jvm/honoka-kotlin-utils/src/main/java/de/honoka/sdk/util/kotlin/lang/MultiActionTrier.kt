package de.honoka.sdk.util.kotlin.lang

/**
 * 依次执行传入的block，只要有一个block执行成功，即忽略后续所有block。
 *
 * 若所有block均未执行成功，则抛出最后一个block所抛出的异常。
 */
class MultiActionTrier {

    companion object {

        @Suppress("UNCHECKED_CAST")
        inline fun start(block: MultiActionTrier.() -> Unit): Any? {
            MultiActionTrier().run {
                block()
                if(success) {
                    return result
                } else {
                    throwable?.let { throw it }
                    return null
                }
            }
        }
    }

    @PublishedApi
    internal var success: Boolean = false

    @PublishedApi
    internal var result: Any? = null

    @PublishedApi
    internal var throwable: Throwable? = null

    inline fun doOne(block: () -> Any?) {
        if(success) return
        try {
            result = block()
            success = true
        } catch(t: Throwable) {
            throwable = t
        }
    }
}
