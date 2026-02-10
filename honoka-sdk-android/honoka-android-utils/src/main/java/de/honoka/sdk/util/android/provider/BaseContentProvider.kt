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
import cn.hutool.json.JSONUtil
import de.honoka.sdk.util.android.various.initGlobalComponents
import de.honoka.sdk.util.kotlin.text.toJsonString
import de.honoka.sdk.util.kotlin.various.ExceptionDetails
import de.honoka.sdk.util.kotlin.various.details

abstract class BaseContentProvider : ContentProvider() {

    data class CallResponse(

        var result: Any? = null,

        var error: ExceptionDetails? = null
    )

    companion object {

        const val CALL_RESPONSE_KEY = "response"
    }

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
                Log.e(this::class.simpleName, "", it)
            }
        }
        val response = CallResponse().apply {
            if(result is Throwable) {
                error = result.details
            } else {
                this.result = result
            }
        }
        bundle.putString(CALL_RESPONSE_KEY, response.toJsonString())
        return bundle
    }

    abstract fun call(method: String?, args: JSON?): Any?
}
