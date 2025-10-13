package de.honoka.sdk.util.android.basic

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.reflect.KClass

abstract class LoopTaskService(private val options: Options) : Service() {

    data class Options(

        val waitDuration: Long,

        val timeUnit: TimeUnit = TimeUnit.SECONDS,

        val stopOnException: Boolean = true
    )

    abstract class AbstractCompanion(internal val clazz: KClass<out LoopTaskService>) {

        @Volatile
        internal var instance: LoopTaskService? = null

        @Volatile
        internal var instanceId: String? = null

        @Synchronized
        fun start() {
            if(instanceId != null) return
            instanceId = UUID.randomUUID().toString()
            global.startService(clazz) {
                putExtra("instanceId", instanceId)
            }
        }

        @Synchronized
        fun stop() {
            instanceId ?: return
            instance!!.stopSelf()
            instanceId = null
            instance = null
        }

        @Synchronized
        fun restart() {
            stop()
            start()
        }

        @Synchronized
        fun restartIfStopped() {
            val stopped = instanceId == null || instance?.destroyed == true
            if(!stopped) return
            restart()
        }
    }

    //在实现类中定义基于AbstractCompanion的companion object，并将其赋值给这个字段
    protected abstract val companion: AbstractCompanion

    private lateinit var thread: Thread

    @Volatile
    internal var destroyed = false

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val instanceId = intent?.getStringExtra("instanceId")
        if(instanceId != companion.instanceId || instanceId == null) {
            stopSelf()
            return START_NOT_STICKY
        }
        companion.instance = this
        super.onStartCommand(intent, flags, startId)
        startThread()
        return START_REDELIVER_INTENT
    }

    private fun startThread() {
        thread = thread {
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
    }

    protected abstract fun doTask()

    override fun onDestroy() {
        super.onDestroy()
        thread.interrupt()
        destroyed = true
    }
}
