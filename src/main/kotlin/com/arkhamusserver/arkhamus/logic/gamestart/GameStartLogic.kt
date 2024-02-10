package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.model.dataaccess.redis.ContainerRedisRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.GameRelatedIdSource
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisGameRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.GameUserRedisRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ContainerRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.StartMarkerRepository
import com.arkhamusserver.arkhamus.model.database.entity.Container
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.ContainerAffectModifiers
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.ItemType.LOOT
import com.arkhamusserver.arkhamus.model.enums.ingame.ItemType.RARE_LOOT
import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class GameStartLogic(
    private val containerRedisRepository: ContainerRedisRepository,
    private val gameRepository: RedisGameRepository,
    private val gameUserRedisRepository: GameUserRedisRepository,
    private val containerRepository: ContainerRepository,
    private val gameRelatedIdSource: GameRelatedIdSource,
    private val startMarkerRepository: StartMarkerRepository,
) {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(GameStartLogic::class.java)
        val random: Random = Random(System.currentTimeMillis())
    }

    fun startGame(game: GameSession) {
        game.gameSessionSettings.level?.levelId?.let { levelId ->
            createTheGame(game)
            createGameUsers(levelId, game)
            createContainers(levelId, game)
        }
    }

    private fun createTheGame(game: GameSession) {
        gameRepository.save(
            RedisGame(game.id.toString())
        )
    }

    private fun createGameUsers(levelId: Long, game: GameSession) {
        val startMarkers = startMarkerRepository.findByLevelId(levelId)
        game.usersOfGameSession.forEach {
            val marker = startMarkers.random(random)
            val userGameSession = RedisGameUser().apply {
                this.id = gameRelatedIdSource.getId(game.id!!, it.userAccount.id!!)
                this.x = marker.x
                this.y = marker.y
            }
            gameUserRedisRepository.save(userGameSession)
            logger.info("user placed to $userGameSession")
        }
    }

    private fun createContainers(
        levelId: Long,
        game: GameSession
    ) {
        val allLevelContainers = containerRepository.findByLevelId(levelId)
        allLevelContainers.forEach { dbContainer ->
            val modifiers = listOf(ContainerAffectModifiers.FULL_RANDOM)
            with(createContainer(game, dbContainer, modifiers)) {
                containerRedisRepository.save(this)
            }
        }
    }

    private fun createContainer(
        game: GameSession,
        dbContainer: Container,
        modifiers: List<ContainerAffectModifiers>
    ) = RedisContainer().apply {
        this.id = gameRelatedIdSource.getId(game.id!!, dbContainer.inGameId!!)
        this.x = dbContainer.x
        this.y = dbContainer.y
        this.interactionRadius = dbContainer.interactionRadius
        this.items = randomizeItems(modifiers)
    }

    private fun randomizeItems(modifiers: List<ContainerAffectModifiers>): Map<String, Long> {
        return when (modifiers.first()) {
            ContainerAffectModifiers.FULL_RANDOM -> {
                val items = Item.values().filter {
                    it.getItemType() in setOf(LOOT, RARE_LOOT)
                }.shuffled(random).subList(0, random.nextInt(3) + 1)
                items.associate { it.getId().toString() to (random.nextLong(3) + 1) }
            }
        }
    }
}