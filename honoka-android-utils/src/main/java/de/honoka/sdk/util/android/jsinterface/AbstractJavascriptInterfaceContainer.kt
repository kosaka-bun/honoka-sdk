package de.honoka.sdk.util.android.jsinterface

import android.webkit.WebView
import de.honoka.sdk.util.android.jsinterface.async.AsyncTaskJsInterface

abstract class AbstractJavascriptInterfaceContainer(private val webView: WebView) {

    private lateinit var interfaceInstances: MutableList<Any>

    lateinit var interfaces: Map<String, Any>

    abstract fun newInterfaceInstances(): List<Any>

    protected fun init() {
        interfaceInstances = newInterfaceInstances().toMutableList()
        interfaces = HashMap<String, Any>().apply {
            interfaceInstances.add(
                AsyncTaskJsInterface(this@AbstractJavascriptInterfaceContainer, webView)
            )
            interfaceInstances.forEach {
                this[it.javaClass.simpleName] = it
            }
        }
        registerJsInterfaces()
    }

    private fun registerJsInterfaces() {
        interfaceInstances.forEach {
            webView.addJavascriptInterface(it, "android_${it.javaClass.simpleName}")
        }
    }
}