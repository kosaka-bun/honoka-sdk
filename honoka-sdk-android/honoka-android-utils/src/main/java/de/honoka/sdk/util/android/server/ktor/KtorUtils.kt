package de.honoka.sdk.util.android.server.ktor

import de.honoka.sdk.util.android.server.RoutingDefinition
import de.honoka.sdk.util.kotlin.reflect.getString
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

object KtorUtils {

    fun parseRoutingDefinition(controller: Any): RoutingDefinition = {
        if(!this::class.hasAnnotation<RestController>()) {
            error("A controller must be annotated with @RestController.")
        }
        val requestMapping = this::class.findAnnotation<RequestMapping>()
        val pathPrefix = requestMapping?.getString("prefix") ?: ""
        this::class.declaredMemberFunctions.forEach {
            parseHandler(it, pathPrefix)
        }
    }
}
