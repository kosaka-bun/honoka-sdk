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
                if(succeeded) {
                    return result
                } else {
                    throwable?.let { throw it }
                    return null
                }
            }
        }
    }

    var succeeded: Boolean = false

    var result: Any? = null

    var throwable: Throwable? = null

    inline fun doOne(block: () -> Any?) {
        if(succeeded) return
        try {
            result = block()
            succeeded = true
        } catch(t: Throwable) {
            throwable = t
        }
    }
}
