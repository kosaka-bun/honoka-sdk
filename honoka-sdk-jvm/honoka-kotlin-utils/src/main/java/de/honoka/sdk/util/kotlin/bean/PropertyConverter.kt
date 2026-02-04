package de.honoka.sdk.util.kotlin.bean

import cn.hutool.cache.CacheUtil
import cn.hutool.core.collection.ConcurrentHashSet
import de.honoka.sdk.util.kotlin.various.error
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.*
import kotlin.reflect.jvm.isAccessible

object PropertyConverter {

    private data class CacheKey(

        var from: KProperty<*>,

        var to: KMutableProperty<*>
    )

    private data class Converter(

        var receiver: Any? = null,

        var function: KFunction<*>
    )

    private val mapperCache = CacheUtil.newLFUCache<CacheKey, Converter>(100)

    private val converterSources = ConcurrentHashSet<Any>().apply {
        add(DefaultConverters)
    }

    fun convert(copyOptions: CopyOptionsExt<*>) {
        if(!copyOptions.convertDifferentTypes) return
        copyOptions.differentTypePropNames.forEach { n ->
            val from = copyOptions.source
            val to = copyOptions.target
            val fromProp = from::class.memberProperties.first { it.name == n }
            val toProp = to::class.memberProperties.first { it.name == n } as KMutableProperty<*>
            val key = CacheKey(fromProp, toProp)
            fun doConvert(converter: Converter) {
                val fromValue = fromProp.getter.call(from)
                if(fromValue == null) {
                    if(copyOptions.ignoreNullValue || toProp.returnType.isMarkedNullable) return
                    error("The type ${toProp.returnType} cannot be null.")
                }
                val result = converter.function.call(converter.receiver, fromValue)
                toProp.setter.call(to, result)
            }
            mapperCache[key]?.let {
                doConvert(it)
                return@forEach
            }
            converterSources.forEach inner@ { s ->
                findConverter(fromProp, toProp, s::class.declaredMemberFunctions)?.let {
                    it.receiver = DefaultConverters
                    doConvert(it)
                    mapperCache.put(key, it)
                    return@forEach
                }
            }
            error("Cannot convert property \"$n\": ${fromProp.returnType} to ${toProp.returnType}")
        }
    }

    private fun findConverter(
        from: KProperty<*>, to: KMutableProperty<*>, collection: Collection<Any>
    ): Converter? {
        collection.forEach {
            val function = if(it is Converter) it.function else it as KFunction<*>
            val matches = function.run {
                if(valueParameters.size != 1) return@run false
                val fParaType = valueParameters[0].type.withNullability(false)
                val fRetType = returnType.withNullability(false)
                val fromType = from.returnType.withNullability(false)
                val toType = to.returnType.withNullability(false)
                fParaType.isSupertypeOf(fromType) && fRetType.isSubtypeOf(toType)
            }
            if(matches) {
                function.isAccessible = true
                return it as? Converter ?: Converter(null, function)
            }
        }
        return null
    }

    fun addConverterSource(source: Any) {
        converterSources.add(source)
    }
}
