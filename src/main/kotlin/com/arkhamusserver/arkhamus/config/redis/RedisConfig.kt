package com.arkhamusserver.arkhamus.config.redis

import jakarta.annotation.PreDestroy
import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import org.springframework.beans.factory.DisposableBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

@Configuration
class RedisConfig : DisposableBean {

    private lateinit var jedisConnectionFactory: JedisConnectionFactory

    @Bean
    fun redisConnectionFactory(
        redisStandaloneConfiguration: RedisStandaloneConfiguration,
        jedisClientConfiguration: JedisClientConfiguration
    ): JedisConnectionFactory {
        jedisConnectionFactory = JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration)
        return jedisConnectionFactory
    }

    @Bean
    fun redisConfiguration(): RedisStandaloneConfiguration {
        val redisStandaloneConfiguration = RedisStandaloneConfiguration("localhost", 6379)
        return redisStandaloneConfiguration
    }

    @Bean
    fun jedisClientConfiguration(
        jedisPoolConfig: GenericObjectPoolConfig<Any>
    ): JedisClientConfiguration {
        val jedisClientConfiguration = JedisClientConfiguration.builder()
            .connectTimeout(Duration.ofSeconds(10)) // connection timeout
            .readTimeout(Duration.ofSeconds(10))    // read timeout
            .usePooling()
            .poolConfig(jedisPoolConfig)
            .build()
        return jedisClientConfiguration
    }

    @Bean
    fun redisPoolConfiguration(): GenericObjectPoolConfig<Any> {
        val jedisPoolConfig = GenericObjectPoolConfig<Any>()
        jedisPoolConfig.maxTotal = 150        // Maximum active connections
        jedisPoolConfig.maxIdle = 50          // Maximum idle connections
        jedisPoolConfig.minIdle = 10          // Minimum idle connections
        jedisPoolConfig.blockWhenExhausted = true  // Whether to block when exhausted
        return jedisPoolConfig
    }

    @Bean
    fun redisTemplate(connectionFactory: JedisConnectionFactory): RedisTemplate<String, Any> {
        val template: RedisTemplate<String, Any> = RedisTemplate<String, Any>().apply {
            setConnectionFactory(connectionFactory)
            keySerializer = StringRedisSerializer()
            valueSerializer = Jackson2JsonRedisSerializer(Any::class.java)
            hashKeySerializer = StringRedisSerializer()
            hashValueSerializer = Jackson2JsonRedisSerializer(Any::class.java)
//            this.setEnableTransactionSupport(true)
        }
        return template
    }


    @PreDestroy
    override fun destroy() {
        jedisConnectionFactory.destroy()
    }
}