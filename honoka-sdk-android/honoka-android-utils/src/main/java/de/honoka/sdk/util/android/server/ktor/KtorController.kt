package de.honoka.sdk.util.android.server.ktor

import de.honoka.sdk.util.android.server.RoutingDefinition
import de.honoka.sdk.util.android.server.respondJson
import io.ktor.server.routing.*
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty1
import kotlin.reflect.full.*

abstract class KtorController {

    companion object {

        @Suppress("UNCHECKED_CAST")
        private fun parsePath(annotation: Annotation): String {
            val prop = annotation.annotationClass.memberProperties.run {
                first { it.name == "path" } as KProperty1<Annotation, String>
            }
            return prop.get(annotation)
        }

        private suspend fun RoutingContext.handle(handler: KFunction<*>) {
            val result = handler.run {
                if(parameters.isEmpty()) {
                    callSuspend(this)
                } else {
                    callSuspend(this, call)
                }
            }
            if(result == Unit) return
            call.respondJson(result)
        }
    }

    val routingDefinition: RoutingDefinition = {
        val classAnnotation = this::class.findAnnotation<RequestMapping>()
        val pathPrefix = classAnnotation?.let { parsePath(it) } ?: ""
        this::class.declaredMemberFunctions.forEach {
            parse(it, pathPrefix)
        }
    }

    private fun Routing.parse(function: KFunction<*>, pathPrefix: String) {
        var annotation: Annotation? = null
        for(it in handlerAnnotations) {
            val annos = function.findAnnotations(it)
            if(annos.isEmpty()) continue
            annotation = annos[0]
            break
        }
        annotation ?: return
        val path = parsePath(annotation)
        when(annotation) {
            is GetMapping -> get("$pathPrefix$path") {
                handle(function)
            }
            is PostMapping -> post("$pathPrefix$path") {
                handle(function)
            }
        }
    }
}

private val handlerAnnotations = listOf(
    GetMapping::class,
    PostMapping::class
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class RequestMapping(val path: String)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class GetMapping(val path: String)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class PostMapping(val path: String)
