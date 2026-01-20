package de.honoka.sdk.spring.starter.core

import de.honoka.sdk.spring.starter.config.MainConfig
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

object ApplicationContextHolder {
    
    @Component("${MainConfig.STARTER_BEAN_NAME_PREFIX}ApplicationContextHolderInjector")
    class Injector : ApplicationContextAware {
        
        override fun setApplicationContext(applicationContext: ApplicationContext) {
            context = applicationContext
        }
    }
    
    lateinit var context: ApplicationContext
}

val <T : Any> KClass<T>.springBean: T
    get() = ApplicationContextHolder.context.getBean(java)
