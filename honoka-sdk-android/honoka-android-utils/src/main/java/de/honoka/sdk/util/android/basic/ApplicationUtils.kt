package de.honoka.sdk.util.android.basic

import android.content.Context

abstract class AbstractApplicationUtils {

    fun initApplication(context: Context) {
        context.initGlobalComponents()
        initApplication()
    }

    protected abstract fun initApplication()
}
