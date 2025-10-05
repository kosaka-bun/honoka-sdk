package de.honoka.sdk.util.android.jsinterface

import android.annotation.SuppressLint
import cn.hutool.json.JSONUtil
import de.honoka.sdk.util.android.basic.toFunctionArgs
import de.honoka.sdk.util.android.ui.AbstractWebActivity
import de.honoka.sdk.util.kotlin.text.singleLine
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.hasAnnotation

internal class JsInterfaceRegistrar(
    private val webActivity: AbstractWebActivity, definedInterfaceInstances: List<Any>
) {

    companion object {

        private val interfaces = ConcurrentHashMap<String, Any>()

        fun invokeAsyncMethod(jsInterfaceName: String, functionName: String, args: String): Any? {
            val jsInterface = interfaces[jsInterfaceName].also {
                it ?: error("Unknown JavaScript interface name: $jsInterfaceName")
            }
            val function = jsInterface!!::class.declaredMemberFunctions.firstOrNull {
                it.name == functionName && it.hasAnnotation<AsyncJavascriptInterface>()
            } ?: error(
                """
                    The interface [$jsInterfaceName] has no function with name [$functionName] |
                    or the function is not annotated by @AsyncJavascriptInterface.
                """.singleLine()
            )
            val rawMethodArgs = JSONUtil.parseArray(args)
            return function.call(jsInterface, *rawMethodArgs.toFunctionArgs(function))
        }
    }

    private val interfaceInstances = listOf(
        BasicJsInterface(webActivity),
        *definedInterfaceInstances.toTypedArray()
    )

    init {
        registerJsInterfaces()
    }

    @SuppressLint("JavascriptInterface")
    private fun registerJsInterfaces() {
        interfaceInstances.forEach {
            webActivity.webView.addJavascriptInterface(
                it, "android_${it::class.simpleName}"
            )
        }
        val interfacesMap = interfaceInstances.associateBy { it::class.simpleName!! }
        interfaces.putAll(interfacesMap)
    }
}

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class AsyncJavascriptInterface
