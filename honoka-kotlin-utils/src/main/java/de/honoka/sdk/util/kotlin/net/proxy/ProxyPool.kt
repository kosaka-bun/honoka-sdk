package de.honoka.sdk.util.kotlin.net.proxy

import cn.hutool.core.collection.ConcurrentHashSet
import cn.hutool.core.lang.Assert
import cn.hutool.http.HttpStatus
import cn.hutool.http.HttpUtil
import de.honoka.sdk.util.kotlin.code.ThreadPoolUtils
import de.honoka.sdk.util.kotlin.code.log
import de.honoka.sdk.util.kotlin.code.tryBlockNullable
import de.honoka.sdk.util.kotlin.net.http.browserApiHeaders
import de.honoka.sdk.util.kotlin.net.socket.SocketForwarder
import de.honoka.sdk.util.kotlin.text.toJsonWrapper
import java.io.Closeable
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class ProxyPool : Closeable {
    
    companion object {
        
        val instance = ProxyPool()
    }
    
    private val pool = ConcurrentHashSet<String>()
    
    private val invalidProxySet = ConcurrentHashSet<String>()
    
    val randomProxy: String?
        get() = tryBlockNullable(3) { pool.randomOrNull() }
    
    private val executor = ThreadPoolUtils.newScheduledPool(1)
    
    @Volatile
    private var forwarderOrNull: SocketForwarder? = null
    
    val forwarder: SocketForwarder
        get() = forwarderOrNull ?: synchronized(this) {
            forwarderOrNull ?: SocketForwarder(pool, timeoutOnEmptyForward = 2 * 1000).also {
                forwarderOrNull = it
            }
        }
    
    @Volatile
    private var runningTask: Future<*>? = null
    
    @Volatile
    private var inited: Boolean = false
    
    @Synchronized
    fun init() {
        if(inited) return
        log.info("Starting ${javaClass.name}...")
        flushProxies(true)
        startMonitoring()
        inited = true
        log.info("The ${javaClass.name} has been started.")
    }
    
    private fun flushProxies(warmUp: Boolean = false) {
        var continuousFailedCount: Int
        ProxySources.all.forEach {
            continuousFailedCount = 0
            val list = runCatching { it() }.getOrElse { return@forEach }
            for(p in list) {
                try {
                    if(p in invalidProxySet) continue
                    checkProxyAndPutInPool(p)
                    continuousFailedCount = 0
                } catch(t: Throwable) {
                    continuousFailedCount++
                }
                if(pool.size >= 3 && warmUp) return
                if(continuousFailedCount >= 8) break
            }
        }
    }
    
    private fun checkProxyAndPutInPool(proxy: String) {
        runCatching { checkProxy(proxy) }.getOrElse {
            log.debug("The proxy $proxy is invalid.", it)
            invalidProxySet.add(proxy)
            throw it
        }
        pool.add(proxy)
    }
    
    private fun checkProxy(proxy: String) {
        val p = proxy.split(":")
        val res = HttpUtil.createGet("https://httpbin.org/ip").run {
            browserApiHeaders()
            setHttpProxy(p[0], p[1].toInt())
            timeout(TimeUnit.SECONDS.toMillis(3).toInt())
            execute()
        }
        Assert.isTrue(res.status == HttpStatus.HTTP_OK)
        res.body().toJsonWrapper().getStr("origin")
    }
    
    private fun startMonitoring() {
        val action = Runnable {
            runCatching { doMonitoring() }
        }
        runningTask = executor.scheduleWithFixedDelay(action, 0, 10, TimeUnit.MINUTES)
    }
    
    private fun doMonitoring() {
        removeInvalidProxies()
        flushProxies()
    }
    
    private fun removeInvalidProxies() {
        pool.toList().forEach {
            runCatching { checkProxy(it) }.getOrElse { t ->
                log.debug("The proxy $it is invalid.", t)
                pool.remove(it)
                invalidProxySet.add(it)
            }
        }
    }
    
    override fun close() {
        runningTask?.cancel(true)
        forwarderOrNull?.close()
        executor.shutdownNow()
    }
}
