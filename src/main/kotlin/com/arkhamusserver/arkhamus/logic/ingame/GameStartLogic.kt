package com.arkhamusserver.arkhamus.logic.ingame

import com.arkhamusserver.arkhamus.model.dataaccess.redis.ContainerRedisRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.GameRelatedIdSource
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ContainerRepository
import com.arkhamusserver.arkhamus.model.database.entity.Container
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.ContainerAffectModifiers
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.ItemType.LOOT
import com.arkhamusserver.arkhamus.model.enums.ingame.ItemType.RARE_LOOT
import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class GameStartLogic(
    private val containerRedisRepository: ContainerRedisRepository,
    private val containerRepository: ContainerRepository,
    private val gameRelatedIdSource: GameRelatedIdSource
) {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(GameStartLogic::class.java)
        val random: Random = Random(System.currentTimeMillis())
    }

    fun startGame(game: GameSession) {
        game.level?.levelId?.let { levelId ->
            val allLevelContainers = containerRepository.findByLevelId(levelId)
            logger.info(
                "creating $allLevelContainers chests for level $levelId"
            )
            allLevelContainers.forEach { dbContainer ->
                logger.info("creating chests ${dbContainer.id}")
                val modifiers = listOf(ContainerAffectModifiers.FULL_RANDOM)
                with(createContainer(game, dbContainer, modifiers)) {
                    containerRedisRepository.save(this)
                }
                logger.info(
                    "created full chest ${
                        containerRedisRepository.findById(
                            gameRelatedIdSource.getId(game.id!!, dbContainer.id!!)
                        ).get()
                    }"
                )
            }
        }
    }

    private fun createContainer(
        game: GameSession,
        dbContainer: Container,
        modifiers: List<ContainerAffectModifiers>
    ) = RedisContainer().apply {
        this.id = gameRelatedIdSource.getId(game.id!!, dbContainer.id!!)
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
                items.associate { it.getId().toString() to (random.nextLong(3)+1) }
            }
        }
    }
}