package de.honoka.sdk.util.kotlin.code

/**
 * 将对象存入ThreadLocal中，然后执行一段代码块，执行完成后清除ThreadLocal中的对象，
 * 最后返回代码块的返回值。
 * 用于在线程池中的线程使用ThreadLocal时，防止未正确释放ThreadLocal中的内容。
 */
inline fun <T> ThreadLocal<T>.use(t: T, block: () -> Any?): Any? {
    try {
        set(t)
        return block()
    } finally {
        remove()
    }
}
