package de.honoka.sdk.util.android.basic

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import kotlin.reflect.KClass

class GlobalComponents internal constructor() {

    companion object {

        internal fun init(context: Context) {
            if(global::application.isInitialized) return
            global.application = context.applicationContext as Application
        }
    }

    lateinit var application: Application
        private set

    val assets: AssetManager
        get() = application.assets

    val contentResolver: ContentResolver
        get() = application.contentResolver

    fun startService(clazz: KClass<*>) {
        application.startService(Intent(application, clazz.java))
    }
}

val global: GlobalComponents = GlobalComponents()

fun Context.initGlobalComponents() {
    GlobalComponents.init(this)
}
