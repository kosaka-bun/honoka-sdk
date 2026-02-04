package de.honoka.sdk.spring.starter.various

import de.honoka.sdk.util.web.ApiResponse
import org.springframework.core.env.PropertyResolver
import org.springframework.http.HttpStatus
import kotlin.reflect.KClass
import kotlin.reflect.full.functions

/**
 * 用于表示这个类被哪个配置类所按条件加载（仅作为标记）
 */
@MustBeDocumented
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class ConditionalComponent(val value: KClass<*>)

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

fun <T> T.toApiResponse(
    msg: String? = null, success: Boolean = true, code: Int = HttpStatus.OK.value()
): ApiResponse<T> = ApiResponse.of<T>().also {
    it.code = code
    it.success = success
    it.msg = msg
    it.data = this
}
