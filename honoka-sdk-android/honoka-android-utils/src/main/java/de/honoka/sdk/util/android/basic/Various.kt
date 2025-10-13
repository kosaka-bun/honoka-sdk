package de.honoka.sdk.util.android.basic

import android.content.Context
import android.webkit.WebView
import android.widget.Toast
import cn.hutool.core.io.FileUtil
import cn.hutool.core.io.IoUtil
import cn.hutool.core.util.ClassUtil
import cn.hutool.json.JSON
import cn.hutool.json.JSONArray
import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil
import kotlinx.coroutines.*
import org.intellij.lang.annotations.Language
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.reflect.ParameterizedType
import java.util.concurrent.TimeUnit
import kotlin.reflect.KFunction
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.javaMethod

fun Context.toast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, duration).show()
}

private fun launchCoroutine(block: suspend () -> Unit, dispatcher: CoroutineDispatcher): Job = run {
    CoroutineScope(dispatcher).launch {
        block()
    }
}

fun launchOnUi(block: suspend () -> Unit): Job = launchCoroutine(block, Dispatchers.Main)

fun launchOnIo(block: suspend () -> Unit): Job = launchCoroutine(block, Dispatchers.IO)

fun WebView.evaluateJsOnUi(
    @Language("JavaScript") script: String,
    callback: (String) -> Unit = {}
) {
    launchOnUi {
        evaluateJavascript(script, callback)
    }
}

fun executeShellCommand(
    @Language("Shell") command: String,
    waitTimeSecends: Int? = null
): String {
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

fun copyAssetsFile(sourcePath: String, targetPath: String, abortIfExists: Boolean = false) {
    if(File(targetPath).exists() && abortIfExists) return
    global.application.assets.open(sourcePath).use {
        val outFile = File(targetPath)
        if(outFile.exists()) outFile.delete()
        FileUtil.touch(outFile)
        FileOutputStream(outFile).use { out ->
            IoUtil.copy(it, out)
        }
    }
}

/**
 * 将集合中的每一项，根据function定义中的参数类型，转换成指定类型的数据，并封装为数组（可处理带泛型的参数类型）
 */
fun Collection<*>.toFunctionArgs(function: KFunction<*>): Array<Any?> {
    val result = ArrayList<Any?>()
    val dataTypes = arrayOf(
        JSONObject::class.java,
        JSONArray::class.java,
        String::class.java
    )
    forEachIndexed { i, arg ->
        val type = function.javaMethod!!.genericParameterTypes[i]
        val shouldAddDirectly = arg == null || run {
            type is Class<*> && (ClassUtil.isBasicType(type) || type in dataTypes)
        }
        if(shouldAddDirectly) {
            result.add(arg)
            return@forEachIndexed
        }
        val rawType = type as? Class<*> ?: (type as ParameterizedType).rawType as Class<*>
        val canBeTransfered = rawType.kotlin.run {
            isData || isSubclassOf(Collection::class)
        }
        if(canBeTransfered) {
            result.add(JSONUtil.toBean(arg as JSON, type, false))
            return@forEachIndexed
        }
        error("Unsupported parameter type [$type] of function [$function]")
    }
    return result.toTypedArray()
}
