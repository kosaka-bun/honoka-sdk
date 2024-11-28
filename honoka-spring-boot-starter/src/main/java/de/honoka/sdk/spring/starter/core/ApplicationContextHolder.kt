package de.honoka.sdk.spring.starter.core

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class ApplicationContextHolder : ApplicationContextAware {
    
    companion object {
        
        lateinit var context: ApplicationContext
        
        val <T : Any> KClass<T>.springBean: T
            get() = context.getBean(java)
    }
    
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        context = applicationContext
    }
}