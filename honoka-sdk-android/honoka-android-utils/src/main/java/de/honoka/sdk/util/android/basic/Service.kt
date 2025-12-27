package de.honoka.sdk.util.android.basic

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.reflect.KClass

abstract class SingletonService : Service() {

    abstract class AbstractCompanion<T : SingletonService>(internal val clazz: KClass<T>) {

        @Volatile
        var instance: T? = null
            internal set

        @Volatile
        internal var instanceId: String? = null

        val starting: Boolean
            get() = instanceId != null && instance?.started != true

        val active: Boolean
            get() = instance?.active == true

        @Synchronized
        fun start() {
            if(instanceId != null) return
            instanceId = UUID.randomUUID().toString()
            global.startService(clazz) {
                putExtra("instanceId", instanceId)
            }
        }

        open fun ensureStarted() {
            while(!active) {
                Thread.sleep(10)
            }
        }

        @Synchronized
        fun stop() {
            instanceId ?: return
            instance!!.stopSelf()
            instanceId = null
            instance = null
        }

        open fun ensureStopped() {
            while(active) {
                Thread.sleep(10)
            }
        }

        @Synchronized
        fun restart() {
            stop()
            ensureStopped()
            start()
            ensureStarted()
        }

        @Synchronized
        fun restartIfStopped() {
            if(starting || active) return
            restart()
        }
    }

    //在实现类中定义基于AbstractCompanion的companion object，并将其赋值给这个字段
    protected abstract val companion: AbstractCompanion<out SingletonService>

    @Volatile
    var started = false
        internal set

    @Volatile
    var destroyed = false
        internal set

    open val active: Boolean
        get() = started && !destroyed

    override fun onBind(intent: Intent): IBinder? = null

    @Suppress("UNCHECKED_CAST")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val instanceId = intent?.getStringExtra("instanceId")
        if(instanceId != companion.instanceId || instanceId == null) {
            stopSelf()
            return START_NOT_STICKY
        }
        (companion as AbstractCompanion<SingletonService>).instance = this
        super.onStartCommand(intent, flags, startId)
        onStartCommandExt(intent, flags, startId)
        started = true
        return START_REDELIVER_INTENT
    }

    protected abstract fun onStartCommandExt(intent: Intent?, flags: Int, startId: Int)

    override fun onDestroy() {
        super.onDestroy()
        onDestroyExt()
        destroyed = true
    }

    protected abstract fun onDestroyExt()
}

abstract class LoopTaskService : SingletonService() {

    data class Options(

        val waitDuration: Long,

        val timeUnit: TimeUnit = TimeUnit.SECONDS,

        val stopOnException: Boolean = true
    )

    abstract override val companion: AbstractCompanion<out LoopTaskService>

    protected abstract val options: Options

    private lateinit var thread: Thread

    override fun onStartCommandExt(intent: Intent?, flags: Int, startId: Int) {
        thread = thread(block = ::threadRun)
    }

    private fun threadRun() {
        while(true) {
            if(thread.isInterrupted) break
            runCatching {
                doTask()
            }.getOrElse {
                Log.e(companion.clazz.simpleName, "", it)
                if(!options.stopOnException) return@getOrElse
                stopSelf()
                thread.interrupt()
                break
            }
            options.timeUnit.sleep(options.waitDuration)
        }
    }

    protected abstract fun doTask()

    override fun onDestroyExt() {
        thread.interrupt()
    }
}
