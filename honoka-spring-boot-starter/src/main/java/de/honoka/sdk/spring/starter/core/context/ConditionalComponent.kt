package de.honoka.sdk.spring.starter.core.context

import kotlin.reflect.KClass

/**
 * 用于表示这个类被哪个配置类所按条件加载（仅作为标记）
 */
@MustBeDocumented
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.TYPE)
annotation class ConditionalComponent(val value: KClass<*>)
