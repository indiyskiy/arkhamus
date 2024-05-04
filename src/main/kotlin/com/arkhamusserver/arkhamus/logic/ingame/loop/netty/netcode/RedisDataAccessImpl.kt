package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode

import com.arkhamusserver.arkhamus.model.dataaccess.redis.*
import com.arkhamusserver.arkhamus.model.redis.RedisAbilityCast
import com.arkhamusserver.arkhamus.model.redis.RedisCraftProcess
import org.springframework.stereotype.Service

@Service
class RedisDataAccessImpl(
    private val gameUserRepository: RedisGameUserRepository,
    private val gameRepository: RedisGameRepository,
    private val containerRepository: RedisContainerRepository,
    private val crafterRepository: RedisCrafterRepository,
    private val lanternRepository: RedisLanternRepository,
    private val timeEventRepository: RedisTimeEventRepository,
    private val abilityCastRepository: RedisAbilityCastRepository,
    private val craftProcessRepository: RedisCraftProcessRepository
) : RedisDataAccess {
    override fun getGameUser(userId: Long?, gameId: Long?) =
        gameUserRepository.findByUserIdAndGameId(userId!!, gameId!!).first()

    override fun getGameUsers(gameId: Long?) =
        gameUserRepository.findByGameId(gameId!!)

    override fun getGame(gameId: Long) = gameRepository.findById(gameId.toString()).get()

    override fun getContainer(containerId: Long, gameId: Long) =
        containerRepository.findByGameIdAndContainerId(gameId, containerId).first()

    override fun getCrafter(crafterId: Long, gameId: Long) =
        crafterRepository.findByGameIdAndCrafterId(gameId, crafterId).first()

    override fun getLantern(lanternId: Long, gameId: Long) =
        lanternRepository.findByGameIdAndLanternId(gameId, lanternId).first()

    override fun getGameContainers(gameId: Long) =
        containerRepository.findByGameId(gameId)

    override fun getGameCrafters(gameId: Long) =
        crafterRepository.findByGameId(gameId)

    override fun getGameLanterns(gameId: Long) =
        lanternRepository.findByGameId(gameId)

    override fun getTimeEvents(gameId: Long) =
        timeEventRepository.findByGameId(gameId)

    override fun getCastedAbilities(gameId: Long): List<RedisAbilityCast> =
        abilityCastRepository.findByGameId(gameId)

    override fun getCraftProcess(gameId: Long): List<RedisCraftProcess> =
        craftProcessRepository.findByGameId(gameId)

}