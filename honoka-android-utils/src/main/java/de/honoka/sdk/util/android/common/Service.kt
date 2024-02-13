package de.honoka.sdk.util.android.common

import android.app.Service
import android.content.Intent
import android.os.IBinder

abstract class BaseService : Service() {

    abstract val companion: BaseServiceCompanion

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        companion.instance = this
        onServiceCreate()
    }

    override fun onDestroy() {
        onServiceDestory()
        companion.instance = null
        companion.instanceCreated = false
    }

    abstract fun onServiceCreate()

    open fun onServiceDestory() {}
}

@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseServiceCompanion {

    internal var instance: BaseService? = null

    internal var instanceCreated = false

    abstract val serviceClass: Class<out BaseService>

    fun createAndStart() {
        GlobalComponents.application.startService(
            Intent(GlobalComponents.application, serviceClass)
        )
    }

    fun checkOrRestart() {
        if(instanceCreated) return
        synchronized(this) {
            if(instanceCreated) return
            instanceCreated = true
        }
        createAndStart()
    }

    fun checkOrRestartAsync() = launchCoroutineOnIoThread { checkOrRestart() }
}