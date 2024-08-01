package com.arkhamusserver.arkhamus.config.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisGameRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.NonGenericMyCrudRepository
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import jakarta.annotation.PostConstruct
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Component

@Component
class RedisCleaner(
    private val redisGameRepository: RedisGameRepository,
    private val toDelete: List<NonGenericMyCrudRepository>,
) {
    @PostConstruct
    fun cleanAll() {
        redisGameRepository.deleteAll()
        toDelete.forEach { repo ->
            if (repo is CrudRepository<*, *>) {
                repo.deleteAll()
            }
        }
    }

    fun cleanGame(gameId: Long) {
        toDelete.forEach { repo ->
            val entities = repo.findByGameId(gameId)
            if (repo is CrudRepository<*, *>) {
                (repo as CrudRepository<Any, String>).deleteAll(entities as MutableIterable<*>)
            }
        }
        redisGameRepository.delete(redisGameRepository.findByGameId(gameId))
    }

    fun cleanGameWithoutGameId(redisGameSession: RedisGame) {
        redisGameRepository.delete(redisGameSession)
    }

}