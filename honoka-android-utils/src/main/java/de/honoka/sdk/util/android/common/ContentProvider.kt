package de.honoka.sdk.util.android.common

import android.content.ContentProvider
import android.content.ContentResolver
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import cn.hutool.core.util.StrUtil
import cn.hutool.json.JSON
import cn.hutool.json.JSONUtil

abstract class BaseContentProvider : ContentProvider() {

    override fun onCreate(): Boolean = false

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
        val result = call(args)?.let { if(it !is Unit) it else null }
        putString("result", result?.let { JSONUtil.toJsonStr(it) })
    }

    abstract fun call(args: JSON?): Any?
}

fun ContentResolver.call(authority: String, args: Any? = null): JSON? {
    val uri = Uri.parse("content://$authority")
    val argsStr = args?.let { JSONUtil.toJsonStr(args) }
    return call(uri, "", argsStr, null)?.let {
        it.getString("result")?.let { res -> JSONUtil.parse(res) }
    }
}

inline fun <reified T> ContentResolver.call(authority: String, args: Any? = null): T? = run {
    call(authority, args)?.toBean(T::class.java)
}

fun contentResolverCall(authority: String, args: Any? = null): JSON? = run {
    GlobalComponents.application.contentResolver.call(authority, args)
}

inline fun <reified T> contentResolverCall(authority: String, args: Any? = null): T? = run {
    contentResolverCall(authority, args)?.toBean(T::class.java)
}