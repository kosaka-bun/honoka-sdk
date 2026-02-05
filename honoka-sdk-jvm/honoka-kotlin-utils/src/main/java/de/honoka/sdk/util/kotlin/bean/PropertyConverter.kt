package de.honoka.sdk.util.kotlin.bean

import cn.hutool.cache.CacheUtil
import cn.hutool.core.collection.ConcurrentHashSet
import de.honoka.sdk.util.kotlin.various.error
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.*
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField

object PropertyConverter {

    private data class CacheKey(

        var from: KProperty<*>,

        var to: KMutableProperty<*>
    )

    private data class Converter(

        var receiver: Any? = null,

        var function: KFunction<*>
    ) {

        fun convert(value: Any): Any? = function.call(receiver, value)
    }

    private val mapperCache = CacheUtil.newLFUCache<CacheKey, Converter>(100)

    private val converterSources = ConcurrentHashSet<Any>().apply {
        add(DefaultConverters)
    }

    fun convertDifferentTypeProps(copyOptions: CopyOptionsExt<*>) {
        if(!copyOptions.convertDifferentTypes) return
        copyOptions.differentTypePropNames.forEach { n ->
            val from = copyOptions.source
            val to = copyOptions.target
            val fromProp = from::class.memberProperties.first { it.name == n }
            val toProp = to::class.memberProperties.first { it.name == n } as KMutableProperty<*>
            val fromValue = fromProp.getter.call(from) ?: run {
                if(copyOptions.ignoreNullValue) return@forEach
                if(toProp.returnType.isMarkedNullable) {
                    toProp.setter.call(to, null)
                    return@forEach
                }
                error("The type ${toProp.returnType} cannot be null.")
            }
            val key = CacheKey(fromProp, toProp)
            mapperCache[key]?.let {
                toProp.setter.call(to, it.convert(fromValue))
                return@forEach
            }
            converterSources.forEach inner@ {
                findConverter(fromProp, toProp, it::class.declaredMemberFunctions)?.let { f ->
                    f.isAccessible = true
                    val converter = Converter(it, f)
                    toProp.setter.call(to, converter.convert(fromValue))
                    mapperCache.put(key, converter)
                    return@forEach
                }
            }
            try {
                val toValue = copyOptions.converter.convert(
                    toProp.javaField!!.genericType, fromValue
                )
                toProp.setter.call(to, toValue)
            } catch(t: Throwable) {
                val msg = "Cannot convert property \"$n\": ${fromProp.returnType} to ${toProp.returnType}"
                error(msg, t)
            }
        }
    }

    private fun findConverter(
        from: KProperty<*>, to: KMutableProperty<*>, functions: Collection<KFunction<*>>
    ): KFunction<*>? = functions.firstOrNull {
        if(it.valueParameters.size != 1) return@firstOrNull false
        val fParaType = it.valueParameters[0].type.withNullability(false)
        val fRetType = it.returnType.withNullability(false)
        val fromType = from.returnType.withNullability(false)
        val toType = to.returnType.withNullability(false)
        fParaType.isSupertypeOf(fromType) && fRetType.isSubtypeOf(toType)
    }

    fun addConverterSource(source: Any) {
        converterSources.add(source)
    }
}
