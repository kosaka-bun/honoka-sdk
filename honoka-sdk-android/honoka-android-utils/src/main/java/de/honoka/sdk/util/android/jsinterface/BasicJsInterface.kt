package de.honoka.sdk.util.android.jsinterface

import android.webkit.JavascriptInterface
import de.honoka.sdk.util.android.activity.AbstractWebActivity
import de.honoka.sdk.util.android.activity.WebActivityExtras
import de.honoka.sdk.util.android.basic.startActivity
import de.honoka.sdk.util.android.server.HttpServer

internal class BasicJsInterface(private val webActivity: AbstractWebActivity) {

    @JavascriptInterface
    fun openNewWebActivity(path: String) {
        val extras = WebActivityExtras(
            HttpServer.Variables.getUrlByPath(path),
            false
        )
        webActivity.startActivity(webActivity::class, extras)
    }

    @JavascriptInterface
    fun finishCurrentWebActivity() {
        webActivity.finish()
    }
}
