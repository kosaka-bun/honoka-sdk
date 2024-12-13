package de.honoka.sdk.util.kotlin.text

import cn.hutool.json.JSON
import cn.hutool.json.JSONArray
import cn.hutool.json.JSONObject

@Suppress("MemberVisibilityCanBePrivate")
class JsonWrapper internal constructor(private val json: JSON) {
    
    operator fun get(path: String): JSONObject = getObj(path)
    
    //not null
    
    fun getObj(path: String): JSONObject = getObjOrNull(path)!!
    
    fun getArray(path: String): JSONArray = getArrayOrNull(path)!!
    
    inline fun <reified T> getBean(path: String): T = getBeanOrNull<T>(path)!!
    
    inline fun <reified T> getList(path: String): List<T> = getListOrNull<T>(path)!!
    
    fun getStr(path: String): String = getStrOrNull(path)!!
    
    fun getInt(path: String): Int = getIntOrNull(path)!!
    
    fun getLong(path: String): Long = getLongOrNull(path)!!
    
    fun getBool(path: String): Boolean = getBoolOrNull(path)!!
    
    fun getDouble(path: String): Double = getDoubleOrNull(path)!!
    
    //nullable
    
    fun getObjOrNull(path: String): JSONObject? = json.getByPath(path) as? JSONObject
    
    fun getArrayOrNull(path: String): JSONArray? = json.getByPath(path) as? JSONArray
    
    inline fun <reified T> getBeanOrNull(path: String): T? = getObjOrNull(path)?.toBean(T::class.java)
    
    inline fun <reified T> getListOrNull(path: String): List<T>? = getArrayOrNull(path)?.toList(T::class.java)
    
    fun getStrOrNull(path: String): String? = json.getByPath(path)?.toString()
    
    fun getIntOrNull(path: String): Int? = (json.getByPath(path) as? Number)?.toInt()
    
    fun getLongOrNull(path: String): Long? = (json.getByPath(path) as? Number)?.toLong()
    
    fun getBoolOrNull(path: String): Boolean? = json.getByPath(path) as? Boolean
    
    fun getDoubleOrNull(path: String): Double? = (json.getByPath(path) as? Number)?.toDouble()
}