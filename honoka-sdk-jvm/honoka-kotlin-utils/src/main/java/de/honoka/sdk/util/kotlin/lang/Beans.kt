package de.honoka.sdk.util.kotlin.lang

import cn.hutool.core.bean.BeanUtil
import cn.hutool.core.bean.copier.CopyOptions
import kotlin.reflect.KProperty
import kotlin.reflect.full.createInstance

inline fun <T : Any> T.copyFrom(from: Any, options: CopyOptions.(T) -> Unit = {}): T {
    val copyOptions = CopyOptions().apply {
        options(this@copyFrom)
    }
    BeanUtil.copyProperties(from, this, copyOptions)
    return this
}

inline fun <T : Any> Any.copyTo(target: T, options: CopyOptions.(T) -> Unit = {}): T {
    val copyOptions = CopyOptions().apply {
        options(target)
    }
    BeanUtil.copyProperties(this, target, copyOptions)
    return target
}

inline fun <reified T : Any> Any.copyTo(options: CopyOptions.(T) -> Unit = {}): T =
    copyTo(T::class.createInstance(), options)

fun CopyOptions.ignore(vararg properties: KProperty<*>) {
    val names = properties.map { it.name }
    setIgnoreProperties(*names.toTypedArray())
}
