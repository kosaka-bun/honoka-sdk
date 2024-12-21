package de.honoka.sdk.util.kotlin.concurrent

import de.honoka.sdk.util.kotlin.basic.WeakReferenceContainer
import java.util.concurrent.locks.ReentrantLock

interface ThreadSafeUsable {

    val lock: ReentrantLock
        get() = WeakReferenceContainer.getOrInit(this, ::lock, ReentrantLock())
}

/*
 * The 'inline' modifier is not allowed on virtual members.
 * Only private or final members can be inlined.
 */
inline fun <T : ThreadSafeUsable, R> T.safeUse(block: T.() -> R): R {
    try {
        lock.lock()
        return block()
    } finally {
        lock.unlock()
    }
}
