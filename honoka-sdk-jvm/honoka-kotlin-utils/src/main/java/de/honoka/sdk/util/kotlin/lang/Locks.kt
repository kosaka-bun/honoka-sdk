package de.honoka.sdk.util.kotlin.lang

import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Lock

class LockFailException private constructor() : RuntimeException() {

    companion object {

        val instance = LockFailException()
    }
}

inline fun <T> Lock.tryLock(
    time: Long? = null, unit: TimeUnit = TimeUnit.SECONDS, action: () -> T
): Result<T> {
    val locked = if(time == null) tryLock() else tryLock(time, unit)
    return if(locked) {
        try {
            Result.success(action())
        } finally {
            unlock()
        }
    } else {
        Result.failure(LockFailException.instance)
    }
}
