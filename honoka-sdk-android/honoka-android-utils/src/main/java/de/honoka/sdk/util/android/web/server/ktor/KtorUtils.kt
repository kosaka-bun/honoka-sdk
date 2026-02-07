package de.honoka.sdk.util.android.web.server.ktor

import cn.hutool.json.JSONUtil
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

object KtorUtils {

    fun parseRoutingDefinition(controller: Any): RoutingDefinition = {
        if(!controller::class.hasAnnotation<RestController>()) {
            error("A controller must be annotated with @RestController.")
        }
        val requestMapping = controller::class.findAnnotation<RequestMapping>()
        controller::class.declaredMemberFunctions.forEach {
            parseHandler(controller, it, requestMapping?.prefix ?: "")
        }
    }
}

typealias RoutingDefinition = Routing.() -> Unit

typealias StatusPageHandler = suspend (ApplicationCall, HttpStatusCode) -> Unit

suspend fun ApplicationCall.respondJson(obj: Any?, httpStatus: HttpStatusCode = HttpStatusCode.OK) {
    if(obj is String) {
        respondText(obj, status = httpStatus)
        return
    }
    respondText(JSONUtil.toJsonStr(obj), ContentType.Application.Json, httpStatus)
}
