package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode

import com.arkhamusserver.arkhamus.model.dataaccess.redis.ContainerRedisRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.GameRelatedIdSource
import com.arkhamusserver.arkhamus.model.dataaccess.redis.GameUserRedisRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.GameRedisRepository
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.springframework.stereotype.Service

@Service
class RedisDataAccess(
    private val gameRelatedIdSource: GameRelatedIdSource,
    private val gameUserRedisRepository: GameUserRedisRepository,
    private val gameRedisRepository: GameRedisRepository,
    private val containerRedisRepository: ContainerRedisRepository
) {
    fun getGameUser(userId: Long, gameId: Long) =
        gameUserRedisRepository.findById(gameRelatedIdSource.getId(gameId, userId)).get()

    fun getGameUser(userId: Long, gameId: String) =
        gameUserRedisRepository.findById(gameRelatedIdSource.getId(gameId, userId)).get()

    fun getGameUsers(gameId: Long) =
        gameUserRedisRepository.findByGameId(gameId)

    fun getOtherGameUsers(userId: Long, gameId: Long): List<RedisGameUser> =
        getGameUsers(gameId).filter { it.userId != userId }

    fun getOtherGameUsers(userId: String, gameId: Long) =
        getGameUsers(gameId).filter { it.id != userId }

    fun getGame(gameId: Long) = gameRedisRepository.findById(gameId.toString()).get()

    fun getContainer(containerId: Long, gameId: Long) =
        containerRedisRepository.findById(gameRelatedIdSource.getId(gameId, containerId)).get()

}