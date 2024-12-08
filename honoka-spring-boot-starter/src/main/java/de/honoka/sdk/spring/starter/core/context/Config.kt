package de.honoka.sdk.spring.starter.core.context

import org.springframework.core.env.PropertyResolver
import kotlin.reflect.KClass
import kotlin.reflect.full.functions

/**
 * 解决PropertyResolver.getProperty()无法获取到yaml中的List类型的属性值的问题
 */
@Suppress("UNCHECKED_CAST")
fun PropertyResolver.getListProperty(key: String): List<String>? {
    getProperty(key, List::class.java)?.let { return it as List<String> }
    val list = ArrayList<String>()
    var i = 0
    while(true) {
        val aValue = getProperty("$key[$i]") ?: break
        list.add(aValue)
        i++
    }
    return if(list.isEmpty()) null else list
}

@Suppress("UNCHECKED_CAST")
fun <T : Enum<T>> PropertyResolver.getEnumListProperty(key: String, clazz: KClass<T>): List<T>? {
    val list = getListProperty(key) ?: return null
    val valueOfFun = clazz.functions.first { f -> f.name == "valueOf" }
    fun find(name: String): T? = runCatching { valueOfFun.call(name) as T }.getOrNull()
    return list.map { (find(it) ?: find(it.uppercase()))!! }
}