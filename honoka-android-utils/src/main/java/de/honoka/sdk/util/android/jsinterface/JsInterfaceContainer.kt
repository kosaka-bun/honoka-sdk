package de.honoka.sdk.util.android.jsinterface

import android.annotation.SuppressLint
import android.webkit.WebView
import de.honoka.sdk.util.android.jsinterface.async.AsyncTaskJsInterface

@SuppressLint("AddJavascriptInterface")
@Suppress("MemberVisibilityCanBePrivate", "CanBeParameter")
class JavascriptInterfaceContainer(val definedInterfaceInstances: List<Any>, private val webView: WebView) {

    private val interfaceInstances: MutableList<Any> = ArrayList(definedInterfaceInstances)

    internal val interfaces: Map<String, Any> = HashMap<String, Any>().also { map ->
        interfaceInstances.add(AsyncTaskJsInterface(this, webView))
        interfaceInstances.forEach {
            map[it.javaClass.simpleName] = it
        }
    }

    init {
        registerJsInterfaces()
    }

    private fun registerJsInterfaces() {
        interfaceInstances.forEach {
            webView.addJavascriptInterface(it, "android_${it.javaClass.simpleName}")
        }
    }
}

abstract class AbstractJavascriptInterfaceContainerFactory {

    protected abstract val containerInstance: JavascriptInterfaceContainer

    abstract val interfaceInstances: List<Any>

    fun getContainer(): JavascriptInterfaceContainer = containerInstance
}