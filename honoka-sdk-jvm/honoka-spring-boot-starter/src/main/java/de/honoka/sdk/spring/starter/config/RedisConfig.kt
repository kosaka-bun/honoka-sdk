package de.honoka.sdk.spring.starter.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator

@ComponentScan(
    "de.honoka.sdk.spring.starter.redis",
    excludeFilters = [
        ComponentScan.Filter(
            type = FilterType.ASPECTJ,
            pattern = ["de.honoka.sdk.spring.starter.redis.redisson..*"]
        )
    ],
    nameGenerator = FullyQualifiedAnnotationBeanNameGenerator::class
)
@ConditionalOnProperty(prefix = RedisProperties.PREFIX, name = ["enabled"])
@Configuration
class RedisConfig

@ComponentScan(
    "de.honoka.sdk.spring.starter.redis.redisson",
    nameGenerator = FullyQualifiedAnnotationBeanNameGenerator::class
)
@ConditionalOnProperty(prefix = RedissonProperties.PREFIX, name = ["enabled"])
@Configuration
class RedissonConfig

@ConfigurationProperties(RedisProperties.PREFIX)
data class RedisProperties(

    var enabled: Boolean = false,

    var keyPrefix: String? = null
) {

    companion object {

        const val PREFIX = "honoka.redis"
    }
}

@ConfigurationProperties(RedissonProperties.PREFIX)
data class RedissonProperties(

    var enabled: Boolean = false
) {

    companion object {

        const val PREFIX = "${RedisProperties.PREFIX}.redisson"
    }
}
