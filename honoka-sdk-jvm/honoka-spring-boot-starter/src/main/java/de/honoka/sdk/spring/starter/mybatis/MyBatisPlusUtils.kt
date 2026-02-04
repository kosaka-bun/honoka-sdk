package de.honoka.sdk.spring.starter.mybatis

import com.baomidou.mybatisplus.annotation.TableId
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaField

object MyBatisPlusUtils {

    fun <T : Any> getTableIdProp(clazz: KClass<T>): KProperty1<T, *> {
        val prop = clazz.declaredMemberProperties.first {
            it.javaField?.isAnnotationPresent(TableId::class.java) == true
        }
        return prop
    }
}
