package de.honoka.sdk.util.android.service

import android.content.Intent
import android.util.Log
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

abstract class LoopTaskService : SingletonService() {

    data class Options(

        val waitDuration: Long,

        val timeUnit: TimeUnit = TimeUnit.SECONDS,

        val stopOnException: Boolean = true
    )

    abstract override val companion: AbstractCompanion<out LoopTaskService>

    protected abstract val options: Options

    private var thread: Thread? = null

    override val active: Boolean
        get() = thread?.isInterrupted == false

    override fun onStartCommandExt(intent: Intent?, flags: Int, startId: Int) {
        thread = thread(block = ::threadRun)
    }

    private fun threadRun() {
        while(true) {
            if(Thread.currentThread().isInterrupted) break
            runCatching {
                doTask()
            }.getOrElse {
                Log.e(companion.clazz.simpleName, "", it)
                if(!options.stopOnException) return@getOrElse
                stopSelf()
                Thread.currentThread().interrupt()
                break
            }
            options.timeUnit.sleep(options.waitDuration)
        }
    }

    protected abstract fun doTask()

    override fun onDestroyExt() {
        thread?.interrupt()
        thread == null
    }
}
