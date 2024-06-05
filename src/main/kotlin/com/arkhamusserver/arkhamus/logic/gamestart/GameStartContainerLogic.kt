package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisContainerRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.ContainerRepository
import com.arkhamusserver.arkhamus.model.database.entity.Container
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.ContainerAffectModifiers
import com.arkhamusserver.arkhamus.model.enums.ingame.ContainerAffectModifiers.*
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.ItemType
import com.arkhamusserver.arkhamus.model.enums.ingame.ItemType.*
import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import com.fasterxml.uuid.Generators
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class GameStartContainerLogic(
    private val redisContainerRepository: RedisContainerRepository,
    private val containerRepository: ContainerRepository,
) {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(GameStartContainerLogic::class.java)
        val random: Random = Random(System.currentTimeMillis())
    }

    fun createContainers(
        levelId: Long, game: GameSession
    ) {
        val allLevelContainers = containerRepository.findByLevelId(levelId)
        allLevelContainers.forEach { dbContainer ->
            val modifiers = listOf(
                when (((dbContainer.inGameId?.toInt() ?: 0) % 10)) {
                    0 -> FULL_RANDOM
                    1 -> LOOT_RANDOM
                    2 -> RARE_LOOT_RANDOM
                    3 -> CULTIST_LOOT_RANDOM
                    4 -> CRAFT_T2_RANDOM
                    5 -> INVESTIGATION_RANDOM
                    6 -> USEFUL_ITEM_RANDOM
                    7 -> CULTIST_ITEM_RANDOM
                    8 -> ADVANCED_USEFUL_ITEM_RANDOM
                    9 -> ADVANCED_CULTIST_ITEM_RANDOM
                    else -> FULL_RANDOM
                }
            )

            with(createContainer(game, dbContainer, modifiers)) {
                redisContainerRepository.save(this)
                logger.info("set chest ${dbContainer.inGameId} to ${this.items.keys.joinToString { it.toString() }}")
            }
        }
    }

    private fun createContainer(
        game: GameSession, dbContainer: Container, modifiers: List<ContainerAffectModifiers>
    ) = RedisContainer(
        id = Generators.timeBasedEpochGenerator().generate().toString(),
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
            FULL_RANDOM -> {
                fullRandom()
            }

            GOD_MODE_CHEST -> {
                godMode()
            }

            LOOT_RANDOM -> mapOfType(LOOT)
            RARE_LOOT_RANDOM -> mapOfType(RARE_LOOT)
            CULTIST_LOOT_RANDOM -> mapOfType(CULTIST_LOOT)
            CRAFT_T2_RANDOM -> mapOfType(CRAFT_T2)
            INVESTIGATION_RANDOM -> mapOfType(INVESTIGATION)
            USEFUL_ITEM_RANDOM -> mapOfType(USEFUL_ITEM)
            CULTIST_ITEM_RANDOM -> mapOfType(CULTIST_ITEM)
            ADVANCED_USEFUL_ITEM_RANDOM -> mapOfType(ADVANCED_USEFUL_ITEM)
            ADVANCED_CULTIST_ITEM_RANDOM -> mapOfType(ADVANCED_CULTIST_ITEM)
        }
    }

    private fun mapOfType(type: ItemType) =
        Item.values().filter { it.itemType == type }.associate { it.id to 99L }.toMutableMap()

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
        }.shuffled(random).subList(0, random.nextInt(3) + 1)
        return items.associate { it.id to (random.nextLong(3) + 1) }.toMutableMap()
    }
}