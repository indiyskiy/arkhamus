package com.arkhamusserver.arkhamus.config.redis

import com.arkhamusserver.arkhamus.model.dataaccess.ToDeleteOnGameEnd
import com.arkhamusserver.arkhamus.model.dataaccess.ToDeleteOnServerStart
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisGameRepository
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component

@Component
class RedisCleaner(
    private val redisGameRepository: RedisGameRepository,
    private val toDeleteOnGameEndList: List<ToDeleteOnGameEnd<*>>,
    private val toDeleteOnServerStartList: List<ToDeleteOnServerStart<*>>,
) {
    @PostConstruct
    fun cleanAll() {
        redisGameRepository.deleteAll()
        toDeleteOnServerStartList.forEach {
            it.deleteAll()
        }
    }

    fun cleanGame(gameId: Long) {
        toDeleteOnGameEndList.forEach {
            it.deleteAll(it.findByGameId(gameId).toMutableList())
        }
        redisGameRepository.delete(redisGameRepository.findByGameId(gameId))
    }

    fun cleanGameWithoutGameId(redisGameSession: RedisGame) {
        redisGameRepository.delete(redisGameSession)
    }

}