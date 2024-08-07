package com.arkhamusserver.arkhamus.config.redis

import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import java.time.Duration

@Configuration
class RedisConfig {
    @Bean
    fun jedisConnectionFactory(): JedisConnectionFactory {
        val redisStandaloneConfiguration = RedisStandaloneConfiguration("localhost", 6379)

        val jedisPoolConfig = GenericObjectPoolConfig<Any>()
        jedisPoolConfig.maxTotal = 150        // Maximum active connections
        jedisPoolConfig.maxIdle = 50          // Maximum idle connections
        jedisPoolConfig.minIdle = 10          // Minimum idle connections
        jedisPoolConfig.blockWhenExhausted = true  // Whether to block when exhausted

        val jedisClientConfiguration = JedisClientConfiguration.builder()
            .connectTimeout(Duration.ofSeconds(2)) // connection timeout
            .readTimeout(Duration.ofSeconds(2))    // read timeout
            .usePooling()
            .poolConfig(jedisPoolConfig)
            .build()

        val jedisConnectionFactory = JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration)
        return jedisConnectionFactory
    }

    @Bean
    fun redisTemplate(connectionFactory: JedisConnectionFactory): RedisTemplate<String, Any> {
        val template: RedisTemplate<String, Any> = RedisTemplate()
        template.connectionFactory = connectionFactory
        return template
    }
}