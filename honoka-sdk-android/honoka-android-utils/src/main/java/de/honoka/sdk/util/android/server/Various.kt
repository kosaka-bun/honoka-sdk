package de.honoka.sdk.util.android.server

import cn.hutool.json.JSONUtil
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

typealias RoutingDefinition = Routing.() -> Unit

typealias StatusPageHandler = suspend (ApplicationCall, HttpStatusCode) -> Unit

suspend fun ApplicationCall.respondJson(obj: Any?, httpStatus: HttpStatusCode = HttpStatusCode.OK) {
    if(obj is String) {
        respondText(obj, status = httpStatus)
        return
    }
    respondText(JSONUtil.toJsonStr(obj), ContentType.Application.Json, httpStatus)
}
