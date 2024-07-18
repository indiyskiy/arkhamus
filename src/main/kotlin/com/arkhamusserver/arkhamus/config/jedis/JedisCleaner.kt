package com.arkhamusserver.arkhamus.config.jedis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.*
import com.arkhamusserver.arkhamus.model.redis.RedisGame
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
    private val redisLevelZoneRepository: RedisLevelZoneRepository,
    private val redisLevelTetragonRepository: RedisLevelTetragonRepository,
    private val redisLevelEllipseRepository: RedisLevelEllipseRepository,

    private val redisQuestRepository: RedisQuestRepository,

    private val redisClueRepository: RedisClueRepository,
) {
    @PostConstruct
    fun cleanAll() {
        redisGameRepository.deleteAll()
        userRedisRepository.deleteAll()
        containerRedisRepository.deleteAll()
        redisTimeEventRepository.deleteAll()
        redisLanternRepository.deleteAll()
        crafterRedisRepository.deleteAll()
        redisCraftProcessRepository.deleteAll()
        redisAbilityCastRepository.deleteAll()

        redisAltarRepository.deleteAll()
        redisAltarHolderRepository.deleteAll()
        redisAltarPollingRepository.deleteAll()
        redisLevelZoneRepository.deleteAll()
        redisLevelTetragonRepository.deleteAll()
        redisLevelEllipseRepository.deleteAll()

        redisClueRepository.deleteAll()

        redisQuestRepository.deleteAll()
    }

    fun cleanGame(gameId: Long) {
        redisGameRepository.delete(redisGameRepository.findByGameId(gameId))

        userRedisRepository.deleteAll(userRedisRepository.findByGameId(gameId))
        containerRedisRepository.deleteAll(containerRedisRepository.findByGameId(gameId))
        redisTimeEventRepository.deleteAll(redisTimeEventRepository.findByGameId(gameId))
        redisLanternRepository.deleteAll(redisLanternRepository.findByGameId(gameId))
        crafterRedisRepository.deleteAll(crafterRedisRepository.findByGameId(gameId))
        redisCraftProcessRepository.deleteAll(redisCraftProcessRepository.findByGameId(gameId))
        redisAbilityCastRepository.deleteAll(redisAbilityCastRepository.findByGameId(gameId))

        redisAltarRepository.deleteAll(redisAltarRepository.findByGameId(gameId))
        redisAltarHolderRepository.deleteAll(redisAltarHolderRepository.findByGameId(gameId))
        redisAltarPollingRepository.deleteAll(redisAltarPollingRepository.findByGameId(gameId))
        redisLevelZoneRepository.deleteAll(redisLevelZoneRepository.findByGameId(gameId))
        redisLevelTetragonRepository.deleteAll(redisLevelTetragonRepository.findByGameId(gameId))
        redisLevelEllipseRepository.deleteAll(redisLevelEllipseRepository.findByGameId(gameId))

        redisClueRepository.deleteAll(redisClueRepository.findByGameId(gameId))
        redisQuestRepository.deleteAll(redisQuestRepository.findByGameId(gameId))
    }

    fun cleanGameWithoutGameId(redisGameSession: RedisGame) {
        redisGameRepository.delete(redisGameSession)
    }

}