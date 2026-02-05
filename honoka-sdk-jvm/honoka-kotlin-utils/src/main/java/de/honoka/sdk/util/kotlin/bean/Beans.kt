package de.honoka.sdk.util.kotlin.bean

import cn.hutool.core.bean.BeanUtil
import kotlin.reflect.full.createInstance

inline fun <T : Any> T.copyFrom(from: Any, options: CopyOptionsExt<T>.() -> Unit = {}): T {
    val copyOptions = CopyOptionsExt(from, this).apply(options)
    BeanUtil.copyProperties(from, this, copyOptions)
    PropertyConverter.convertDifferentTypeProps(copyOptions)
    return this
}

inline fun <T : Any> Any.copyTo(to: T, options: CopyOptionsExt<T>.() -> Unit = {}): T {
    val copyOptions = CopyOptionsExt(this, to).apply(options)
    BeanUtil.copyProperties(this, to, copyOptions)
    PropertyConverter.convertDifferentTypeProps(copyOptions)
    return to
}

inline fun <reified T : Any> Any.copyTo(options: CopyOptionsExt<T>.() -> Unit = {}): T =
    copyTo(T::class.createInstance(), options)
