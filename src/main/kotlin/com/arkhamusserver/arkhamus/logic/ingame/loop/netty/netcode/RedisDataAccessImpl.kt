package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode

import com.arkhamusserver.arkhamus.model.dataaccess.redis.*
import com.arkhamusserver.arkhamus.model.redis.RedisAbilityCast
import com.arkhamusserver.arkhamus.model.redis.RedisCraftProcess
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class RedisDataAccessImpl(
    private val gameUserRepository: RedisGameUserRepository,
    private val gameRepository: RedisGameRepository,
    private val containerRepository: RedisContainerRepository,
    private val crafterRepository: RedisCrafterRepository,
    private val lanternRepository: RedisLanternRepository,
    private val altarRepository: RedisAltarRepository,
    private val timeEventRepository: RedisTimeEventRepository,
    private val abilityCastRepository: RedisAbilityCastRepository,
    private val craftProcessRepository: RedisCraftProcessRepository
) : RedisDataAccess {
    override fun getGameUser(userId: Long?, gameId: Long?) =
        gameUserRepository.findByUserIdAndGameId(userId!!, gameId!!).first()

    override fun getGameUsers(gameId: Long?) =
        gameUserRepository.findByGameId(gameId!!)

    override fun getGame(gameId: Long) = gameRepository.findById(gameId.toString()).getOrNull()

    override fun getContainer(containerId: Long, gameId: Long) =
        containerRepository.findByGameIdAndContainerId(gameId, containerId).first()

    override fun getCrafter(crafterId: Long, gameId: Long) =
        crafterRepository.findByGameIdAndCrafterId(gameId, crafterId).first()

    override fun getLantern(lanternId: Long, gameId: Long) =
        lanternRepository.findByGameIdAndLanternId(gameId, lanternId).first()

    override fun getAltar(altarId: Long, gameId: Long) =
        altarRepository.findByGameIdAndAltarId(gameId, altarId).first()

    override fun getGameAltars(gameId: Long) =
        altarRepository.findByGameId(gameId)

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


    override fun deleteGame(gameId: Long) {
        gameRepository.deleteById(gameId.toString())
    }

    override fun deleteGameUsers(gameId: Long) {
        val gameUserIds = gameUserRepository.findByGameId(gameId).map { it.id }
        gameUserRepository.deleteAllById(gameUserIds)
    }

    override fun deleteContainers(gameId: Long) {
        val containerIds = containerRepository.findByGameId(gameId).map { it.id }
        containerRepository.deleteAllById(containerIds)
    }

    override fun deleteLanterns(gameId: Long) {
        val lanternIds = lanternRepository.findByGameId(gameId).map { it.id }
        lanternRepository.deleteAllById(lanternIds)
    }

    override fun deleteTimeEvents(gameId: Long) {
        val timeEventIds = timeEventRepository.findByGameId(gameId).map { it.id }
        timeEventRepository.deleteAllById(timeEventIds)
    }
}