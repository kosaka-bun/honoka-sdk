package de.honoka.sdk.util.android.jsinterface

import android.webkit.JavascriptInterface
import de.honoka.sdk.util.android.server.HttpServer
import de.honoka.sdk.util.android.ui.AbstractWebActivity
import de.honoka.sdk.util.android.ui.WebActivityExtras
import de.honoka.sdk.util.android.ui.startActivity

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
