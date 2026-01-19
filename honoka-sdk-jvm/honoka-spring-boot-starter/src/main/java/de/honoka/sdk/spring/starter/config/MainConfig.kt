package de.honoka.sdk.spring.starter.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@ComponentScan("de.honoka.sdk.spring.starter.core")
@Configuration("${MainConfig.STARTER_BEAN_NAME_PREFIX}MainConfig")
class MainConfig {
    
    companion object {
        
        const val STARTER_BEAN_NAME_PREFIX = "honokaStarter"
    }
}
