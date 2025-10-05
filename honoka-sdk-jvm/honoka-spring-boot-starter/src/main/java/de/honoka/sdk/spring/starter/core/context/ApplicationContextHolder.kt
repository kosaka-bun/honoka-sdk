package de.honoka.sdk.spring.starter.core.context

import de.honoka.sdk.spring.starter.core.context.ApplicationContextHolder.context
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

object ApplicationContextHolder {
    
    @Component
    class Injector : ApplicationContextAware {
        
        override fun setApplicationContext(applicationContext: ApplicationContext) {
            context = applicationContext
        }
    }
    
    lateinit var context: ApplicationContext
}

val <T : Any> KClass<T>.springBean: T
    get() = context.getBean(java)
