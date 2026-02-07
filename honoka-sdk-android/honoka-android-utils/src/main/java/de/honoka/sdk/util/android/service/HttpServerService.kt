package de.honoka.sdk.util.android.service

import de.honoka.sdk.util.android.web.server.DefaultHttpServer

/**
 * 仅用于启动和守护[DefaultHttpServer]的运行，停止或重启该服务不会连带停止或重启[DefaultHttpServer]。
 */
class HttpServerService : LoopTaskService() {

    companion object : AbstractCompanion<HttpServerService>(HttpServerService::class)

    override val companion: AbstractCompanion<out LoopTaskService> = Companion

    override val options: Options = Options(5)

    override fun doTask() {
        DefaultHttpServer.restartIfStopped()
    }
}
