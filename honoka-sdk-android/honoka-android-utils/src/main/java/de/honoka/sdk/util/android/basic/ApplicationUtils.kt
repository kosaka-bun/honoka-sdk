package de.honoka.sdk.util.android.basic

import android.content.Context

abstract class AbstractApplicationUtils {

    @Volatile
    var inited: Boolean = false
        private set

    @Synchronized
    fun initApplication(context: Context) {
        if(inited) return
        context.initGlobalComponents()
        initApplication()
        inited = true
    }

    protected abstract fun initApplication()
}
