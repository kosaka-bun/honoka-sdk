package de.honoka.sdk.util.android.jsinterface.async

import android.webkit.JavascriptInterface
import android.webkit.WebView
import cn.hutool.core.thread.BlockPolicy
import cn.hutool.core.util.ClassUtil
import cn.hutool.json.JSONUtil
import de.honoka.sdk.util.android.code.evaluateJavascriptOnUiThread
import de.honoka.sdk.util.android.jsinterface.AbstractJavascriptInterfaceContainer
import java.lang.reflect.Method
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class AsyncTaskJsInterface(
    private val jsInterfaceContainer: AbstractJavascriptInterfaceContainer,
    private val webView: WebView
) {

    private val threadPool = ThreadPoolExecutor(
        5, 30, 60, TimeUnit.SECONDS,
        LinkedBlockingQueue(), BlockPolicy()
    )

    @JavascriptInterface
    fun invokeAsyncMethod(jsInterfaceName: String, methodName: String, callbackId: String, args: String) {
        threadPool.submit {
            val result = AsyncTaskResult()
            try {
                val jsInterface = jsInterfaceContainer.interfaces[jsInterfaceName].also {
                    it ?: throw Exception("Unknown JavaScript interface name: $jsInterfaceName")
                }
                val method: Method = jsInterface!!.javaClass.declaredMethods.run {
                    forEach {
                        if(it.name == methodName) return@run it
                    }
                    throw Exception("Unknown method name \"$methodName\" of interface name: $jsInterfaceName")
                }
                val annotation = method.getAnnotation(AsyncJavascriptInterface::class.java).also {
                    it ?: throw Exception("Method \"$methodName\" in interface \"$jsInterfaceName\" is not asynchronous")
                }
                result.run {
                    isPlainText = annotation!!.isPlainText
                    val methodArgs = JSONUtil.parseArray(args).map { it?.toString() }.toTypedArray()
                    this.result = method.invoke(jsInterface, *methodArgs)?.let {
                        if(ClassUtil.isBasicType(it.javaClass)) it.toString() else JSONUtil.toJsonStr(it)
                    }
                    isResolve = true
                }
            } catch(t: Throwable) {
                result.run {
                    isResolve = false
                    message = t.message
                }
            }
            val resultStr = JSONUtil.toJsonStr(result)
            val script = "window.jsInterfaceAsyncMethodCallbackUtils.invokeCallback('$callbackId', $resultStr)"
            webView.evaluateJavascriptOnUiThread(script)
        }
    }
}