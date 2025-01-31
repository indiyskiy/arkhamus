package com.arkhamusserver.arkhamus.config.redis

import com.arkhamusserver.arkhamus.config.netty.TcpNettyServer
import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.NonGenericMyCrudRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RedisGameRepository
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class RedisCleaner(
    private val redisGameRepository: RedisGameRepository,
    private val toDelete: List<NonGenericMyCrudRepository>,
) {
    @PostConstruct
    @Transactional
    fun cleanAll() {
        redisGameRepository.deleteAll()
        toDelete.forEach { repo ->
            if (repo is CrudRepository<*, *>) {
                repo.deleteAll()
            }
        }
    }

    @Transactional
    fun cleanGame(gameId: Long) {
        toDelete.forEach { repo ->
            val entities = repo.findByGameId(gameId)
            if (repo is CrudRepository<*, *>) {
                (repo as CrudRepository<Any, String>).deleteAll(entities as MutableIterable<*>)
            }
        }
        redisGameRepository.findByGameId(gameId).firstOrNull()?.let { redisGameRepository.delete(it) } ?: {
            logger.warn("Game repository contains no game for id $gameId")
        }

    }

    companion object {
        var logger: Logger = LoggerFactory.getLogger(RedisCleaner::class.java)
    }

}