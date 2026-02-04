package de.honoka.sdk.util.kotlin.various

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
object DirectProxy {

    private class Handler(private val target: () -> Any) : InvocationHandler {

        override fun invoke(proxy: Any, method: Method, args: Array<Any?>): Any? =
            method.invoke(target(), *args)
    }

    fun <T : Any> of(target: () -> Any, clazz: KClass<T>): T {
        val proxy = Proxy.newProxyInstance(
            clazz.java.classLoader, arrayOf(clazz.java), Handler(target)
        )
        return proxy as T
    }
}
