package de.honoka.sdk.spring.starter.redis.redisson

import de.honoka.sdk.spring.starter.config.RedisProperties
import de.honoka.sdk.util.kotlin.various.MultiActionTrier
import org.redisson.api.NameMapper
import org.redisson.config.Config
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer
import org.springframework.stereotype.Component

@Component
class NameMapperCustomizer(
    private val redisProperties: RedisProperties
) : RedissonAutoConfigurationCustomizer {

    private inner class NameMapperImpl : NameMapper {

        private val prefix = "${redisProperties.keyPrefix}:"

        override fun map(name: String): String = "$prefix$name"

        override fun unmap(name: String): String = name.removePrefix(prefix)
    }

    override fun customize(configuration: Config) {
        if(redisProperties.keyPrefix.isNullOrBlank()) return
        val mapper = NameMapperImpl()
        configuration.run {
            when {
                isSingleConfig -> useSingleServer().nameMapper = mapper
                isClusterConfig -> useClusterServers().nameMapper = mapper
                isSentinelConfig -> useSentinelServers().nameMapper = mapper
                else -> MultiActionTrier.start {
                    doOne { useMasterSlaveServers().nameMapper = mapper }
                    doOne { useReplicatedServers().nameMapper = mapper }
                }
            }
        }
    }
}
