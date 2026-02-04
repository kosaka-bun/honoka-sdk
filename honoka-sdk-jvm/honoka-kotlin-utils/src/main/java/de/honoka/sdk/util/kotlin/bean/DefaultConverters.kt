package de.honoka.sdk.util.kotlin.bean

import cn.hutool.json.JSONArray
import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil

internal object DefaultConverters {

    fun strToJsonObj(str: String): JSONObject = JSONUtil.parseObj(str)

    fun strToJsonArr(str: String): JSONArray = JSONUtil.parseArray(str)
}
