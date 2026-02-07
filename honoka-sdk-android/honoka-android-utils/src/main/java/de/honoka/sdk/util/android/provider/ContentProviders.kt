package de.honoka.sdk.util.android.provider

import android.content.ContentResolver
import androidx.core.net.toUri
import de.honoka.sdk.util.kotlin.text.toJsonString
import de.honoka.sdk.util.kotlin.text.toJsonWrapper
import de.honoka.sdk.util.kotlin.various.DetailedException
import de.honoka.sdk.util.kotlin.various.tryCastOrNull
import kotlin.reflect.KClass
import kotlin.reflect.KType

private class CallFailedException : DetailedException()

fun ContentResolver.call(authority: String, method: String? = null, args: Any? = null): Any? {
    val uri = "content://$authority".toUri()
    val result = call(uri, method ?: "", args?.toJsonString(), null)?.let {
        val response = it.getString(BaseContentProvider.CALL_RESPONSE_KEY)!!.toJsonWrapper()
            .toBean<BaseContentProvider.CallResponse>()
        response.error?.let { e ->
            throw CallFailedException().apply {
                details = e
            }
        }
        response.result
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
