package de.honoka.sdk.util.kotlin.bean

import cn.hutool.core.bean.copier.CopyOptions
import cn.hutool.core.convert.TypeConverter
import kotlin.reflect.KProperty1
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberProperties

@Suppress("PROPERTY_HIDES_JAVA_FIELD")
class CopyOptionsExt<T : Any>(internal val source: Any, val target: T) : CopyOptions() {

    internal var ignoreNullValue: Boolean
        get() = super.ignoreNullValue
        set(value) {
            super.ignoreNullValue = value
        }

    internal var converter: TypeConverter
        get() = super.converter
        set(value) {
            super.converter = value
        }

    @PublishedApi
    internal val differentTypePropNames = HashSet<String>()

    private val ignoredPropNames = HashSet<String>()

    var convertDifferentTypes = true

    init {
        ignoreNullValue = true
        initDifferentTypePropNames()
    }

    private fun initDifferentTypePropNames() {
        val sourceProps = source::class.memberProperties.associateBy { it.name }
        target::class.memberProperties.forEach {
            val shouldIgnore = it.name in sourceProps && !sourceProps[it.name]!!.returnType
                .isSubtypeOf(it.returnType)
            if(!shouldIgnore) return@forEach
            differentTypePropNames.add(it.name)
        }
        ignoredPropNames.addAll(differentTypePropNames)
        updateIgnore()
    }

    private fun updateIgnore() {
        setIgnoreProperties(*ignoredPropNames.toTypedArray())
    }

    fun ignore(vararg properties: KProperty1<T, *>) {
        val names = properties.map { it.name }
        ignoredPropNames.addAll(names)
        updateIgnore()
    }
}
