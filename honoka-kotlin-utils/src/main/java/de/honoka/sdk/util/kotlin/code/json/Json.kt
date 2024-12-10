package de.honoka.sdk.util.kotlin.code.json

import cn.hutool.json.JSON
import cn.hutool.json.JSONArray
import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil

fun String.toJsonObject(): JSONObject = JSONUtil.parseObj(this)

fun String.toJsonArray(): JSONArray = JSONUtil.parseArray(this)

fun JSON.wrapper(): JsonWrapper = JsonWrapper(this)

fun String.toJsonWrapper(): JsonWrapper = JSONUtil.parse(this).wrapper()

fun Any?.toJsonString(pretty: Boolean = false): String = run {
    if(pretty) {
        JSONUtil.toJsonPrettyStr(this)
    } else {
        JSONUtil.toJsonStr(this)
    }
}