package com.arkhamusserver.arkhamus.config.jedis

import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import com.arkhamusserver.arkhamus.model.dataaccess.redis.*

@Component
class JedisCleaner(
    private val userRedisRepository: RedisGameUserRepository,
    private val containerRedisRepository: RedisContainerRepository,
    private val crafterRedisRepository: RedisCrafterRepository,
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
        crafterRedisRepository.deleteAll()
    }

}