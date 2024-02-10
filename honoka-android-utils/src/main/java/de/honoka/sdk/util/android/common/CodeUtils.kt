package de.honoka.sdk.util.android.common

import android.webkit.WebView
import cn.hutool.core.io.FileUtil
import cn.hutool.core.io.IoUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

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

fun runShellCommandForResult(command: String, waitTimeSecends: Int? = null): String {
    Runtime.getRuntime().exec(command).run {
        val processOut = ByteArrayOutputStream()
        if(waitTimeSecends == null) {
            Thread.sleep(100)
            while(isAlive) {
                processOut.write(inputStream.readBytes())
                TimeUnit.SECONDS.sleep(1)
            }
            processOut.write(inputStream.readBytes())
        } else {
            waitFor(waitTimeSecends.toLong(), TimeUnit.SECONDS)
            processOut.write(inputStream.readBytes())
            if(isAlive) destroyForcibly()
        }
        return String(processOut.toByteArray()).trim()
    }
}

fun copyAssetsFileTo(sourceFilePath: String, targetFilePath: String, abortIfTargetExist: Boolean = false) {
    if(File(targetFilePath).exists() && abortIfTargetExist) return
    GlobalComponents.application.assets.open(sourceFilePath).use {
        val outFile = File(targetFilePath)
        if(outFile.exists()) outFile.delete()
        FileUtil.touch(outFile)
        FileOutputStream(outFile).use { out ->
            IoUtil.copy(it, out)
        }
    }
}