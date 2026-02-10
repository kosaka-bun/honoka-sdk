package de.honoka.sdk.util.kotlin.various

import cn.hutool.core.exceptions.ExceptionUtil
import cn.hutool.json.JSON
import de.honoka.sdk.util.kotlin.reflect.isSubclassOfAny
import de.honoka.sdk.util.kotlin.text.wrapper
import de.honoka.sdk.util.kotlin.web.ApiResponse
import kotlin.math.min
import kotlin.reflect.KClass

data class ExceptionDetails(

    var message: String? = null,

    var stackTrace: List<String>? = null
) {

    constructor(t: Throwable) : this() {
        val stackTraceLines = ExceptionUtil.stacktraceToString(
            t, 3000, mapOf('\t' to "")
        ).lines()
        message = stackTraceLines[0]
        stackTrace = stackTraceLines.subList(1, stackTraceLines.size)
    }

    fun toApiResponse(lineCount: Int? = null): ApiResponse<Any?> {
        lineCount?.let {
            stackTrace = stackTrace?.run {
                subList(0, min(lineCount, size))
            }
        }
        return ApiResponse.fail(message, this).apply {
            message = null
        }
    }

    fun throwThis(): Nothing {
        throw DetailedException(this)
    }
}

open class DetailedException(var details: ExceptionDetails? = null) : RuntimeException() {

    override val message: String?
        get() = details?.message
}

val Throwable.messageWithName: String
    get() = "${this::class.qualifiedName}: $message"

val Throwable.details: ExceptionDetails
    get() = when(this) {
        is DetailedException -> details!!
        else -> ExceptionDetails(this)
    }

val ApiResponse<*>.exceptionDetails: ExceptionDetails
    get() {
        val result = when(error) {
            is ExceptionDetails -> error as ExceptionDetails
            else -> (error as JSON).wrapper().toBean<ExceptionDetails>()
        }
        result.message = msg
        return result
    }

val ExceptionDetails.stackTraceStr: String
    get() = "$message\n${stackTrace?.joinToString("\n")}"

fun <T : Throwable> Throwable?.isAny(vararg types: KClass<out T>): Boolean =
    this?.let { it::class.isSubclassOfAny(*types) } ?: false

fun <T : Throwable> Throwable?.isAny(types: Collection<KClass<out T>>): Boolean =
    isAny(*types.toTypedArray())

fun error(message: Any? = null, cause: Throwable? = null): Nothing {
    throw IllegalStateException(message.toString(), cause)
}
