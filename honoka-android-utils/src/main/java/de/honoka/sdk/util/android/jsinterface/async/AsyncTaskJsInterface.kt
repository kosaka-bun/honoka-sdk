package de.honoka.sdk.util.android.jsinterface.async

import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import cn.hutool.core.thread.BlockPolicy
import cn.hutool.core.util.ClassUtil
import cn.hutool.core.util.StrUtil
import cn.hutool.json.JSON
import cn.hutool.json.JSONArray
import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil
import de.honoka.sdk.util.android.common.evaluateJavascriptOnUiThread
import de.honoka.sdk.util.android.jsinterface.JavascriptInterfaceContainer
import java.io.Serializable
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class AsyncTaskJsInterface(
    private val jsInterfaceContainer: JavascriptInterfaceContainer,
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
                method.getAnnotation(AsyncJavascriptInterface::class.java).also {
                    it ?: throw Exception("Method \"$methodName\" in interface \"$jsInterfaceName\" is not asynchronous")
                }
                result.run {
                    val rawMethodArgs = JSONUtil.parseArray(args)
                    val methodArgs = ArrayList<Any>().apply {
                        rawMethodArgs.forEachIndexed { i, arg ->
                            val type = method.genericParameterTypes[i]
                            val shouldAddDirectly = type is Class<*> && (
                                ClassUtil.isBasicType(type) || arrayOf(
                                    JSONObject::class.java,
                                    JSONArray::class.java,
                                    String::class.java
                                ).contains(type)
                            )
                            if(shouldAddDirectly) {
                                add(arg)
                                return@forEachIndexed
                            }
                            val rawType = if(type is Class<*>) type else (type as ParameterizedType).rawType as Class<*>
                            val canBeTransfered = Serializable::class.java.isAssignableFrom(rawType) ||
                                Collection::class.java.isAssignableFrom(rawType)
                            if(canBeTransfered) {
                                add(JSONUtil.toBean(arg as JSON, type, false))
                                return@forEachIndexed
                            }
                            throw Exception("Unsupported parameter type \"$type\" of $method")
                        }
                    }
                    this.result = method.invoke(jsInterface, *methodArgs.toTypedArray())
                    isResolve = true
                }
            } catch(t: Throwable) {
                val throwable = if(t is InvocationTargetException) t.cause ?: t else t
                Log.e("", "", throwable)
                result.run {
                    isResolve = false
                    message = throwable.message.let {
                        if(StrUtil.isBlank(it)) throwable.javaClass.simpleName else it
                    }
                }
            }
            val resultStr = JSONUtil.toJsonStr(result)
            val script = "window.jsInterfaceAsyncMethodCallbackUtils.invokeCallback('$callbackId', $resultStr)"
            webView.evaluateJavascriptOnUiThread(script)
        }
    }
}