package de.honoka.sdk.spring.starter.redis

import de.honoka.sdk.spring.starter.config.RedisProperties
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.stereotype.Component

/**
 * Component的名称必须是`redisTemplate`，参见[RedisAutoConfiguration.redisTemplate]。
 */
@Component("redisTemplate")
class DefaultRedisTemplate(
    private val redisProperties: RedisProperties,
    redisConnectionFactory: RedisConnectionFactory
) : RedisTemplate<String, Any>() {

    private inner class KeySerializer : StringRedisSerializer() {

        private val prefix = "${redisProperties.keyPrefix}:"

        override fun serialize(value: String?): ByteArray? =
            super.serialize(value?.let { "$prefix$it" })

        override fun deserialize(bytes: ByteArray?): String? =
            super.deserialize(bytes)?.removePrefix(prefix)
    }

    init {
        connectionFactory = redisConnectionFactory
        keySerializer = if(redisProperties.keyPrefix.isNullOrBlank()) {
            RedisSerializer.string()
        } else {
            KeySerializer()
        }
        hashKeySerializer = RedisSerializer.string()
        valueSerializer = RedisSerializer.json()
        hashValueSerializer = RedisSerializer.json()
        afterPropertiesSet()
    }
}
