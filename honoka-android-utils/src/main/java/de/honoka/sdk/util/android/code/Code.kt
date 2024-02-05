package de.honoka.sdk.util.android.code

import android.webkit.WebView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun launchCoroutineOnUiThread(block: suspend () -> Unit) {
    CoroutineScope(Dispatchers.Main).launch {
        block()
    }
}

fun WebView.evaluateJavascriptOnUiThread(script: String, callback: (String) -> Unit = {}) {
    launchCoroutineOnUiThread {
        evaluateJavascript(script, callback)
    }
}