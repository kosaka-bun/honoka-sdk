package de.honoka.sdk.spring.starter.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@ComponentScan("de.honoka.sdk.spring.starter.redisson")
@EnableConfigurationProperties(RedissonProperties::class)
@ConditionalOnProperty(prefix = RedissonProperties.PREFIX, name = ["enabled"])
@Configuration("${MainConfig.STARTER_BEAN_NAME_PREFIX}RedissonConfig")
class RedissonConfig

@ConfigurationProperties(RedissonProperties.PREFIX)
data class RedissonProperties(

    var enabled: Boolean = false
) {

    companion object {

        const val PREFIX = "honoka.redisson"
    }
}
