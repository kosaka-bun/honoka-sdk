package de.honoka.sdk.spring.starter.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@ComponentScan("de.honoka.sdk.spring.starter.redis")
@EnableConfigurationProperties(RedisProperties::class)
@ConditionalOnProperty(prefix = RedisProperties.PREFIX, name = ["enabled"])
@Configuration("${MainConfig.STARTER_BEAN_NAME_PREFIX}RedisConfig")
class RedisConfig

@ConfigurationProperties(RedisProperties.PREFIX)
data class RedisProperties(

    var enabled: Boolean = false,

    var keyPrefix: String? = null
) {

    companion object {

        const val PREFIX = "honoka.redis"
    }
}
