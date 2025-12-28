package de.honoka.sdk.util.android.server.ktor

import de.honoka.sdk.util.android.server.respondJson
import de.honoka.sdk.util.kotlin.reflect.callSuspendAdaptive
import de.honoka.sdk.util.kotlin.reflect.findAnyAnnotation
import de.honoka.sdk.util.kotlin.reflect.getString
import io.ktor.server.routing.*
import kotlin.reflect.KFunction

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class RestController

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class RequestMapping(val prefix: String)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class GetMapping(val path: String)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class PostMapping(val path: String)

private val handlerAnnotations = arrayOf(
    GetMapping::class,
    PostMapping::class
)

internal fun Routing.parseHandler(function: KFunction<*>, pathPrefix: String) {
    val annotation = function.findAnyAnnotation(*handlerAnnotations) ?: return
    val path = annotation.getString("path")
    when(annotation) {
        is GetMapping -> get("$pathPrefix$path") {
            handle(function)
        }
        is PostMapping -> post("$pathPrefix$path") {
            handle(function)
        }
    }
}

private suspend fun RoutingContext.handle(handler: KFunction<*>) {
    val result = handler.callSuspendAdaptive(this, call)
    if(result == Unit) return
    call.respondJson(result)
}
