package de.honoka.sdk.util.android.common

import android.app.Application
import android.content.Context

object GlobalComponents {

    lateinit var application: Application

    fun initApplicationFieldByContextIfNotInited(context: Context) {
        runCatching {
            application.packageName
            return
        }
        application = context.applicationContext as Application
    }
}