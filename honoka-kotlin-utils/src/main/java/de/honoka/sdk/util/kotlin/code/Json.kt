package de.honoka.sdk.util.kotlin.code

import cn.hutool.json.JSONArray
import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil

fun String.toJsonObject(): JSONObject = JSONUtil.parseObj(this)

fun String.toJsonArray(): JSONArray = JSONUtil.parseArray(this)
