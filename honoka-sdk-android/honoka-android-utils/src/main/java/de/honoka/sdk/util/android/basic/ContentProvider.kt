package de.honoka.sdk.util.android.basic

import android.content.ContentProvider
import android.content.ContentResolver
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.core.net.toUri
import cn.hutool.core.exceptions.ExceptionUtil
import cn.hutool.core.util.StrUtil
import cn.hutool.json.JSON
import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil
import de.honoka.sdk.util.kotlin.basic.tryCastOrNull
import kotlin.reflect.KClass
import kotlin.reflect.KType

abstract class BaseContentProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        context!!.initGlobalComponents()
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? = null

    override fun getType(uri: Uri): String? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int = 0

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0

    override fun call(method: String, arg: String?, extras: Bundle?): Bundle {
        val bundle = Bundle()
        val args = if(StrUtil.isNotBlank(arg)) JSONUtil.parse(arg) else null
        val result = try {
            call(method.ifBlank { null }, args)?.let {
                if(it !is Unit) it else null
            }
        } catch(t: Throwable) {
            ExceptionUtil.getRootCause(t).also {
                Log.e(javaClass.simpleName, "", it)
            }
        }
        val json = JSONObject().also {
            if(result !is Throwable) {
                it["result"] = result
            } else {
                it["error"] = JSONObject().also { jo ->
                    jo["info"] = ExceptionUtil.getMessage(result)
                    jo["stackTrace"] = ExceptionUtil.stacktraceToString(result)
                }
            }
        }
        bundle.putString("json", json.toString())
        return bundle
    }

    abstract fun call(method: String?, args: JSON?): Any?
}

data class ContentProviderCallException(

    val info: String,

    val stackTraceText: String
) : RuntimeException(info)

fun ContentResolver.call(authority: String, method: String? = null, args: Any? = null): Any? {
    val uri = "content://$authority".toUri()
    val argsStr = args?.let { JSONUtil.toJsonStr(args) }
    val result = call(uri, method ?: "", argsStr, null)?.let {
        it.getString("json").let { jsonStr ->
            val json = JSONUtil.parseObj(jsonStr)
            json.getJSONObject("error")?.let { error ->
                throw ContentProviderCallException(
                    error.getStr("info"),
                    error.getStr("stackTrace")
                )
            }
            json["result"]
        }
    }
    return result
}

fun <T : Any> ContentResolver.typedCallOrNull(
    authority: String, method: String? = null, args: Any? = null, resultClass: KClass<T>
): T? = call(authority, method, args).tryCastOrNull(resultClass)

fun <T : Any> ContentResolver.typedCallOrNull(
    authority: String, method: String? = null, args: Any? = null, resultType: KType
): T? = call(authority, method, args).tryCastOrNull(resultType)

/*
 * 需注意，若实化泛型T中含有嵌套泛型，比如调用该方法时表现为：typedCall<List<Entity>>()，则在代码中获取
 * T::class时，只能获取到泛型T的顶级类型，即List的Class对象。
 */
inline fun <reified T : Any> ContentResolver.typedCallOrNull(
    authority: String, method: String? = null, args: Any? = null
): T? = typedCallOrNull(authority, method, args, T::class)

fun <T : Any> ContentResolver.typedCall(
    authority: String, method: String? = null, args: Any? = null, resultClass: KClass<T>
): T = typedCallOrNull(authority, method, args, resultClass)!!

fun <T : Any> ContentResolver.typedCall(
    authority: String, method: String? = null, args: Any? = null, resultType: KType
): T = typedCallOrNull(authority, method, args, resultType)!!

inline fun <reified T : Any> ContentResolver.typedCall(
    authority: String, method: String? = null, args: Any? = null
): T = typedCallOrNull(authority, method, args)!!
