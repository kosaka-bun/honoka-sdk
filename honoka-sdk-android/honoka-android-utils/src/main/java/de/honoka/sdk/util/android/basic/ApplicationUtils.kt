package de.honoka.sdk.util.android.basic

import android.content.Context
import android.util.Log

abstract class AbstractApplicationUtils {

    @Volatile
    var inited: Boolean = false
        private set

    @Synchronized
    fun initApplication(context: Context) {
        if(inited) return
        try {
            context.initGlobalComponents()
            initApplication()
        } catch(t: Throwable) {
            Log.e(
                this::class.simpleName,
                "Application failed to initialize.",
                t
            )
            throw t
        }
        inited = true
    }

    protected abstract fun initApplication()
}
