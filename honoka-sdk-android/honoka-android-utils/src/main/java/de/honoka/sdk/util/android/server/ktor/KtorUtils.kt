package de.honoka.sdk.util.android.server.ktor

import de.honoka.sdk.util.android.server.RoutingDefinition
import de.honoka.sdk.util.kotlin.reflect.getString
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

object KtorUtils {

    fun parseRoutingDefinition(controller: Any): RoutingDefinition = {
        if(!controller::class.hasAnnotation<RestController>()) {
            error("A controller must be annotated with @RestController.")
        }
        val requestMapping = controller::class.findAnnotation<RequestMapping>()
        val pathPrefix = requestMapping?.getString("prefix") ?: ""
        controller::class.declaredMemberFunctions.forEach {
            parseHandler(controller, it, pathPrefix)
        }
    }
}
