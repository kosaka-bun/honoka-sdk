package de.honoka.sdk.util.android.various

import android.content.Context
import android.util.Log

abstract class AbstractApplicationUtils {

    @Volatile
    var inited: Boolean = false
        private set

    @Volatile
    var foregroundInited: Boolean = false
        private set

    @Synchronized
    fun initApplication(context: Context, foreground: Boolean = true) {
        if(inited && foregroundInited) return
        try {
            context.initGlobalComponents()
            if(!inited) {
                initApplication()
                inited = true
            }
            if(foreground && !foregroundInited) {
                initForegroundApplication()
                foregroundInited = true
            }
        } catch(t: Throwable) {
            Log.e(
                this::class.simpleName,
                "Application failed to initialize.",
                t
            )
            throw t
        }
    }

    protected abstract fun initApplication()

    protected open fun initForegroundApplication() {}
}
