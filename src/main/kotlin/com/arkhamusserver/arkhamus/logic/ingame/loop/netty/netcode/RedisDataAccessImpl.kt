package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode

import com.arkhamusserver.arkhamus.model.dataaccess.redis.*
import com.arkhamusserver.arkhamus.model.dataaccess.redis.utils.GameRelatedIdSource
import com.arkhamusserver.arkhamus.model.redis.RedisAbilityCast
import org.springframework.stereotype.Service

@Service
class RedisDataAccessImpl(
    private val gameRelatedIdSource: GameRelatedIdSource,
    private val gameUserRepository: RedisGameUserRepository,
    private val gameRepository: RedisGameRepository,
    private val containerRepository: RedisContainerRepository,
    private val lanternRepository: RedisLanternRepository,
    private val timeEventRepository: RedisTimeEventRepository,
    private val abilityCastRepository: RedisAbilityCastRepository
) : RedisDataAccess {
    override fun getGameUser(userId: Long?, gameId: Long?) =
        gameUserRepository.findById(gameRelatedIdSource.getId(gameId, userId)).get()

    override fun getGameUsers(gameId: Long?) =
        gameUserRepository.findByGameId(gameId!!)

    override fun getGame(gameId: Long) = gameRepository.findById(gameId.toString()).get()

    override fun getContainer(containerId: Long, gameId: Long) =
        containerRepository.findById(gameRelatedIdSource.getId(gameId, containerId)).get()

    override fun getLantern(lanternId: Long, gameId: Long) =
        lanternRepository.findById(gameRelatedIdSource.getId(gameId, lanternId)).get()

    override fun getGameContainers(gameId: Long) =
        containerRepository.findByGameId(gameId)

    override fun getGameLanterns(gameId: Long) =
        lanternRepository.findByGameId(gameId)

    override fun getTimeEvents(gameId: Long) =
        timeEventRepository.findByGameId(gameId)

    override fun getCastedAbilities(gameId: Long): List<RedisAbilityCast> =
        abilityCastRepository.findByGameId(gameId)

}