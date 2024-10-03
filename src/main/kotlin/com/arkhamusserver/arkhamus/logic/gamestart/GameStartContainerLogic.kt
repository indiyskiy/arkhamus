package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisContainerRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.ContainerRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.game.Container
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.ContainerTag
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.core.ItemType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.ItemType.*
import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import com.fasterxml.uuid.Generators
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.random.Random

@Component
class GameStartContainerLogic(
    private val redisContainerRepository: RedisContainerRepository,
    private val containerRepository: ContainerRepository,
    private val lootTableHandler: LootTableHandler
) {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(GameStartContainerLogic::class.java)
        val random: Random = Random(System.currentTimeMillis())
    }

    @Transactional
    fun createContainers(
        levelId: Long, game: GameSession
    ) {
        val allLevelContainers = containerRepository.findByLevelId(levelId)
        allLevelContainers.forEachIndexed { i, dbContainer ->
            createContainer(game, dbContainer)
        }
    }

    private fun createContainer(
        game: GameSession, dbContainer: Container
    ) = RedisContainer(
        id = Generators.timeBasedEpochGenerator().generate().toString(),
        containerId = dbContainer.inGameId,
        containerTags = dbContainer.containerTags.map { it.name }.toMutableList(),
        gameId = game.id!!,
        x = dbContainer.x,
        y = dbContainer.y,
        z = dbContainer.z,
        interactionRadius = dbContainer.interactionRadius,
        gameTags = mutableListOf(),
    ).apply {
        this.items = randomizeItems(dbContainer)
        redisContainerRepository.save(this)
    }

    private fun randomizeItems(
        dbContainer: Container
    ): MutableMap<Int, Int> {
        return if (!dbContainer.containerTags.isEmpty()) {
            randomItems(dbContainer.containerTags)
        } else {
            godModChest(dbContainer)
        }
    }

    private fun randomItems(
        containerTags: Set<ContainerTag>
    ): MutableMap<Int, Int> {
        return lootTableHandler.generateLoot(containerTags)
    }

    private fun godModChest(dbContainer: Container): MutableMap<Int, Int> {
        val num = (dbContainer.id ?: 0) % 9
        return when (num) {
            0L -> mapOfType(LOOT)
            1L -> mapOfType(RARE_LOOT)
            2L -> mapOfType(CULTIST_LOOT)
            3L -> mapOfType(CRAFT_T2)
            4L -> mapOfType(INVESTIGATION)
            5L -> mapOfType(USEFUL_ITEM)
            6L -> mapOfType(CULTIST_ITEM)
            7L -> mapOfType(ADVANCED_USEFUL_ITEM)
            8L -> mapOfType(ADVANCED_CULTIST_ITEM)
            else -> mapOfType(TECH_TYPE)
        }
    }

    private fun mapOfType(type: ItemType) =
        Item.values().filter { it.itemType == type }.associate { it.id to 50 }.toMutableMap()

}