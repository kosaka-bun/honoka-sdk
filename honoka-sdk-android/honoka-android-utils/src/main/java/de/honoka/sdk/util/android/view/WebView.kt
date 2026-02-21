package de.honoka.sdk.util.android.view

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

class DefaultWebViewClient : WebViewClient() {

    //重写此方法，解决WebView在重定向时打开系统浏览器的问题
    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        val url = request.url.toString()
        //禁止WebView加载未知协议的URL
        if(!url.startsWith("http")) {
            return true
        }
        view.loadUrl(url)
        return true
    }
}
