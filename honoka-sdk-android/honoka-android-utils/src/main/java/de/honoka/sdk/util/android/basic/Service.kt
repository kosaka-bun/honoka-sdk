package de.honoka.sdk.util.android.basic

import android.app.Service
import android.content.Intent
import android.os.IBinder
import kotlin.reflect.KClass

abstract class BaseService : Service() {

    //应当在实现类中定义基于BaseServiceCompanion的companion object，并将其赋值给这个字段
    protected abstract val companion: BaseServiceCompanion

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        companion.instance = this
        onServiceCreate()
    }

    override fun onDestroy() {
        onServiceDestory()
        companion.run {
            instance = null
            started = false
        }
    }

    abstract fun onServiceCreate()

    open fun onServiceDestory() {}
}

class BaseServiceCompanion(private val serviceClass: KClass<out BaseService>) {

    internal var instance: BaseService? = null

    @Volatile
    internal var started = false

    @Synchronized
    fun start() {
        if(started) return
        global.startService(serviceClass)
        started = true
    }
}
