package de.honoka.sdk.util.kotlin.reflect

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.full.*
import kotlin.reflect.typeOf

@Suppress("UNCHECKED_CAST")
@JvmInline
value class Reflector<T> internal constructor(val value: T) {

    @PublishedApi
    internal fun getProps(findSuper: Boolean): Collection<KProperty<*>> =
        if(findSuper) value!!::class.memberProperties else value!!::class.declaredMemberProperties

    @PublishedApi
    internal fun getFuns(findSuper: Boolean): Collection<KFunction<*>> =
        if(findSuper) value!!::class.memberFunctions else value!!::class.declaredMemberFunctions

    inline fun <reified R> get(name: String, findSuper: Boolean = false): Reflector<R> {
        val prop = getProps(findSuper).first { it.name == name && it.returnType == typeOf<R>() }
        return prop.access().getter.call(value).reflector as Reflector<R>
    }

    inline fun <reified R> call(name: String, vararg params: Any?, findSuper: Boolean = false): Reflector<R> {
        val function = getFuns(findSuper).first {
            val not = it.name != name || it.valueParameters.size != params.size ||
                !it.returnType.isSubtypeOf(typeOf<R>())
            if(not) return@first false
            params.forEachIndexed { i, p ->
                val type = it.valueParameters[i].type
                if(p == null) {
                    if(type.isMarkedNullable) {
                        return@forEachIndexed
                    } else {
                        return@first false
                    }
                }
                if(!p::class.isSubclassOf(type.classifier as KClass<*>)) return@first false
            }
            true
        }
        return function.access().call(value, *params) as Reflector<R>
    }

    fun callForAny(name: String, vararg params: Any?, findSuper: Boolean = false): Reflector<Any?> =
        call(name, params = params, findSuper)

    fun getDirectSubclass(base: Class<*>): Class<*>? {
        var clazz: Class<*> = value!!::class.java
        repeat(10) {
            val superclass = clazz.superclass ?: return null
            if(superclass == base) return clazz
            clazz = superclass
        }
        return null
    }
}

val <T> T.reflector: Reflector<T>
    get() = Reflector(this)
