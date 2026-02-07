package de.honoka.sdk.util.android.web.webview

import android.webkit.JavascriptInterface
import de.honoka.sdk.util.android.activity.AbstractWebActivity
import de.honoka.sdk.util.android.activity.WebActivityExtras
import de.honoka.sdk.util.android.activity.startActivity
import de.honoka.sdk.util.android.web.server.DefaultHttpServer

internal class BasicJsInterface(private val webActivity: AbstractWebActivity) {

    @JavascriptInterface
    fun openNewWebActivity(path: String) {
        val extras = WebActivityExtras(
            DefaultHttpServer.getUrlByPath(path),
            false
        )
        webActivity.startActivity(webActivity::class, extras)
    }

    @JavascriptInterface
    fun finishCurrentWebActivity() {
        webActivity.finish()
    }
}
