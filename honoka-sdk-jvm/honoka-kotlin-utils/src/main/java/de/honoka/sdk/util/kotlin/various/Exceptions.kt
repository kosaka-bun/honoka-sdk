package de.honoka.sdk.util.kotlin.various

import cn.hutool.core.exceptions.ExceptionUtil
import de.honoka.sdk.util.kotlin.reflect.isSubclassOfAny
import kotlin.reflect.KClass

data class ExceptionDetails(

    var message: String? = null,

    var stackTrace: List<String>? = null
) {

    constructor(t: Throwable) : this(t.message) {
        val charsToReplace = mapOf('\r' to "", '\t' to "", '\"' to "'")
        stackTrace = ExceptionUtil.stacktraceToString(
            t, 3000, charsToReplace
        ).split('\n')
    }
}

open class DetailedException(var details: ExceptionDetails? = null) : RuntimeException() {

    override val message: String?
        get() = details?.message
}

val Throwable.messageWithName: String
    get() = "${this::class.qualifiedName}: $message"

fun <T : Throwable> Throwable?.isAny(vararg types: KClass<out T>): Boolean =
    this?.let { it::class.isSubclassOfAny(*types) } ?: false

fun <T : Throwable> Throwable?.isAny(types: Collection<KClass<out T>>): Boolean =
    isAny(*types.toTypedArray())

fun error(message: Any? = null, cause: Throwable? = null): Nothing {
    throw IllegalStateException(message.toString(), cause)
}
