package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisContainerRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.utils.GameRelatedIdSource
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisGameUserRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisGameRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisTimeEventRepository
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.springframework.stereotype.Service

@Service
class RedisDataAccess(
    private val gameRelatedIdSource: GameRelatedIdSource,
    private val gameUserRepository: RedisGameUserRepository,
    private val gameRepository: RedisGameRepository,
    private val containerRepository: RedisContainerRepository,
    private val timeEventRepository: RedisTimeEventRepository
) {
    fun getGameUser(userId: Long?, gameId: Long?) =
        gameUserRepository.findById(gameRelatedIdSource.getId(gameId, userId)).get()

    fun getGameUsers(gameId: Long?) =
        gameUserRepository.findByGameId(gameId!!)

    fun getOtherGameUsers(userId: Long?, gameId: Long?): List<RedisGameUser> =
        getGameUsers(gameId).filter { it.userId != userId }

    fun getOtherGameUsers(userId: String, gameId: Long) =
        getGameUsers(gameId).filter { it.id != userId }

    fun getGame(gameId: Long) = gameRepository.findById(gameId.toString()).get()

    fun getContainer(containerId: Long, gameId: Long) =
        containerRepository.findById(gameRelatedIdSource.getId(gameId, containerId)).get()

    fun getGameContainers(gameId: Long) =
        containerRepository.findByGameId(gameId)
    fun getTimeEvents(gameId: Long) =
        timeEventRepository.findByGameId(gameId)

    fun loadGlobalGameData(game: RedisGame): GlobalGameData {
        val gameId = game.id.toLong()
        val allUsers = getGameUsers(gameId)
        val allContainers = getGameContainers(gameId)
        val allEvents = getTimeEvents(gameId)
        return GlobalGameData(game).apply {
            this.users = allUsers.associateBy { it.userId }
            this.containers = allContainers.associateBy { it.containerId }
            this.timeEvents = allEvents
        }
    }

}