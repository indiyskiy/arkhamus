package com.arkhamusserver.arkhamus.config.jedis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.*
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component

@Component
class JedisCleaner(
    private val userRedisRepository: RedisGameUserRepository,
    private val containerRedisRepository: RedisContainerRepository,
    private val crafterRedisRepository: RedisCrafterRepository,
    private val redisLanternRepository: RedisLanternRepository,
    private val redisGameRepository: RedisGameRepository,
    private val redisTimeEventRepository: RedisTimeEventRepository,
    private val redisCraftProcessRepository: RedisCraftProcessRepository,
    private val redisAbilityCastRepository: RedisAbilityCastRepository,

    private val redisAltarRepository: RedisAltarRepository,
    private val redisAltarHolderRepository: RedisAltarHolderRepository,
    private val redisAltarPollingRepository: RedisAltarPollingRepository,
    private val redisLevelZoneRepository: RedisLevelZoneRepository

    ) {
    @PostConstruct
    fun cleanAll() {
        userRedisRepository.deleteAll()
        containerRedisRepository.deleteAll()
        redisGameRepository.deleteAll()
        redisTimeEventRepository.deleteAll()
        redisLanternRepository.deleteAll()
        crafterRedisRepository.deleteAll()
        redisCraftProcessRepository.deleteAll()
        redisAbilityCastRepository.deleteAll()

        redisAltarRepository.deleteAll()
        redisAltarHolderRepository.deleteAll()
        redisAltarPollingRepository.deleteAll()
        redisLevelZoneRepository.deleteAll()
    }

}