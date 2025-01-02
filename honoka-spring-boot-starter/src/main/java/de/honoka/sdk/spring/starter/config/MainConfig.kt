package de.honoka.sdk.spring.starter.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@ComponentScan("de.honoka.sdk.spring.starter.core")
@EnableConfigurationProperties(MainProperties::class)
@Configuration("${MainConfig.STARTER_BEAN_NAME_PREFIX}MainConfig")
class MainConfig {
    
    companion object {
        
        const val STARTER_BEAN_NAME_PREFIX = "honokaStarter"
    }
}

@ConfigurationProperties(MainProperties.PREFIX)
class MainProperties {
    
    companion object {
        
        const val PREFIX = "honoka.starter"
    }
}
