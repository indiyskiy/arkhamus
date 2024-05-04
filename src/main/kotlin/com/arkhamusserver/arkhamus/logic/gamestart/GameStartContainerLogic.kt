package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisContainerRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.utils.GameRelatedIdSource
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ContainerRepository
import com.arkhamusserver.arkhamus.model.database.entity.Container
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.ContainerAffectModifiers
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.ItemType.*
import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class GameStartContainerLogic(
    private val redisContainerRepository: RedisContainerRepository,
    private val containerRepository: ContainerRepository,
    private val gameRelatedIdSource: GameRelatedIdSource,
) {

    private val random: Random = Random(System.currentTimeMillis())

    fun createContainers(
        levelId: Long,
        game: GameSession
    ) {
        val allLevelContainers = containerRepository.findByLevelId(levelId)
        allLevelContainers.forEachIndexed { i, dbContainer ->
            val modifiers = if (i == 0)
                listOf(ContainerAffectModifiers.GOD_MODE_CHEST)
            else
                listOf(ContainerAffectModifiers.FULL_RANDOM)

            with(createContainer(game, dbContainer, modifiers)) {
                redisContainerRepository.save(this)
            }
        }
    }

    private fun createContainer(
        game: GameSession,
        dbContainer: Container,
        modifiers: List<ContainerAffectModifiers>
    ) = RedisContainer(
        id = gameRelatedIdSource.getId(game.id!!, dbContainer.inGameId!!),
        containerId = dbContainer.inGameId!!,
        gameId = game.id!!
    ).apply {
        this.x = dbContainer.x!!
        this.y = dbContainer.y!!
        this.interactionRadius = dbContainer.interactionRadius!!
        this.items = randomizeItems(modifiers)
    }

    private fun randomizeItems(modifiers: List<ContainerAffectModifiers>): MutableMap<Int, Long> {
        return when (modifiers.first()) {
            ContainerAffectModifiers.FULL_RANDOM -> {
                fullRandom()
            }
            ContainerAffectModifiers.GOD_MODE_CHEST -> {
                godMode()
            }
        }
    }

    private fun godMode(): MutableMap<Int, Long> {
        val items = Item.values().filter {
            it.itemType in setOf(
                LOOT,
                RARE_LOOT,
                CULTIST_LOOT,
                CRAFT_T2,
                INVESTIGATION,
                USEFUL_ITEM,
                CULTIST_ITEM,
                ADVANCED_USEFUL_ITEM,
                ADVANCED_CULTIST_ITEM
            )
        }
        return items.associate { it.id to 99L }.toMutableMap()
    }

    private fun fullRandom(): MutableMap<Int, Long> {
        val items = Item.values().filter {
            it.itemType in setOf(LOOT, RARE_LOOT)
        }.shuffled(random)
            .subList(0, random.nextInt(3) + 1)
        return items.associate { it.id to (random.nextLong(3) + 1) }.toMutableMap()
    }
}