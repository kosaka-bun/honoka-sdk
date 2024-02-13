package de.honoka.sdk.util.android.common

import android.content.ContentProvider
import android.content.ContentResolver
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import cn.hutool.core.util.StrUtil
import cn.hutool.json.JSON
import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil

abstract class BaseContentProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        GlobalComponents.initApplicationFieldByContextIfNotInited(context!!)
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

    override fun call(method: String, arg: String?, extras: Bundle?): Bundle = Bundle().apply {
        val args = if(StrUtil.isNotBlank(arg)) JSONUtil.parse(arg) else null
        val result = call(method.ifBlank { null }, args)?.let { if(it !is Unit) it else null }
        putString("json", JSONObject().also {
            it["result"] = result
        }.toString())
    }

    abstract fun call(method: String?, args: JSON?): Any?
}

object ContentProviderUtils {

    inline fun <reified T> getTypedResult(result: Any): T = result.let {
        when {
            T::class.java.isAssignableFrom(it.javaClass) -> it as T
            it is JSON -> it.toBean(T::class.java)
            else -> throw ClassCastException("Cannot cast ${it.javaClass.name} to ${T::class.java.name}")
        }
    }
}

fun ContentResolver.call(authority: String, method: String? = null, args: Any? = null): Any? {
    val uri = Uri.parse("content://$authority")
    val argsStr = args?.let { JSONUtil.toJsonStr(args) }
    return call(uri, method ?: "", argsStr, null)?.let {
        it.getString("json").let { json -> JSONUtil.parseObj(json)["result"] }
    }
}

//该方法上的clazz参数仅用于防止与返回Any类型的call函数的签名产生冲突，不实际使用
@Suppress("UNUSED_PARAMETER")
inline fun <reified T> ContentResolver.call(
    authority: String, method: String? = null, args: Any? = null, clazz: Class<T>? = null
): T = call(authority, method, args).let {
    ContentProviderUtils.getTypedResult<T>(it!!)
}

fun contentResolverCall(authority: String, method: String? = null, args: Any? = null): Any? = run {
    GlobalComponents.application.contentResolver.call(authority, method, args)
}

@Suppress("UNUSED_PARAMETER")
inline fun <reified T> contentResolverCall(
    authority: String, method: String? = null, args: Any? = null, clazz: Class<T>? = null
): T = contentResolverCall(authority, method, args).let {
    ContentProviderUtils.getTypedResult<T>(it!!)
}