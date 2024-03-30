package com.arkhamusserver.arkhamus.config.jedis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.*
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component

@Component
class JedisCleaner(
    private val userRedisRepository: RedisGameUserRepository,
    private val containerRedisRepository: RedisContainerRepository,
    private val redisLanternRepository: RedisLanternRepository,
    private val redisGameRepository: RedisGameRepository,
    private val redisTimeEventRepository: RedisTimeEventRepository
) {
    @PostConstruct
    fun cleanAll() {
        userRedisRepository.deleteAll()
        containerRedisRepository.deleteAll()
        redisGameRepository.deleteAll()
        redisTimeEventRepository.deleteAll()
        redisLanternRepository.deleteAll()
    }

}