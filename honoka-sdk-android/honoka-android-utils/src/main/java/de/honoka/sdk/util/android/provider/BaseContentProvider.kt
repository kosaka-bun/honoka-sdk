package de.honoka.sdk.util.android.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log
import cn.hutool.core.exceptions.ExceptionUtil
import cn.hutool.core.util.StrUtil
import cn.hutool.json.JSON
import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil
import de.honoka.sdk.util.android.basic.initGlobalComponents

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
