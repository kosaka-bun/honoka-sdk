package de.honoka.sdk.util.android.web.server.ktor

import de.honoka.sdk.util.kotlin.reflect.callSuspendAdaptive
import de.honoka.sdk.util.kotlin.reflect.findAnyAnnotation
import de.honoka.sdk.util.kotlin.reflect.getString
import io.ktor.server.routing.*
import kotlin.reflect.KFunction

private val handlerAnnotations = arrayOf(
    GetMapping::class,
    PostMapping::class
)

internal fun Routing.parseHandler(controller: Any, function: KFunction<*>, pathPrefix: String) {
    val annotation = function.findAnyAnnotation(*handlerAnnotations) ?: return
    val path = annotation.getString("path")
    when(annotation) {
        is GetMapping -> get("$pathPrefix$path") {
            handle(controller, function)
        }
        is PostMapping -> post("$pathPrefix$path") {
            handle(controller, function)
        }
    }
}

private suspend fun RoutingContext.handle(controller: Any, handler: KFunction<*>) {
    val result = handler.callSuspendAdaptive(controller, call)
    if(result == Unit) return
    call.respondJson(result)
}
