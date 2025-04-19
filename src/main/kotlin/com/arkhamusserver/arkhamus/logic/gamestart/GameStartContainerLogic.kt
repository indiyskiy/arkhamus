package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.generateRandomId
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameContainerRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.ContainerRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Container
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.core.ItemType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.ItemType.*
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.ContainerTag
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.ingame.InGameContainer
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.InventoryCell
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.random.Random

@Component
class GameStartContainerLogic(
    private val inGameContainerRepository: InGameContainerRepository,
    private val containerRepository: ContainerRepository,
    private val lootTableHandler: LootTableHandler
) {

    companion object {
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
    ) = InGameContainer(
        id = generateRandomId(),
        containerId = dbContainer.inGameId,
        containerTags = dbContainer.containerTags.map { it.name }.toMutableSet(),
        gameId = game.id!!,
        x = dbContainer.x,
        y = dbContainer.y,
        z = dbContainer.z,
        interactionRadius = dbContainer.interactionRadius,
        gameTags = mutableSetOf(),
        visibilityModifiers = setOf(VisibilityModifier.ALL)
    ).apply {
        this.items = randomizeItems(dbContainer)
        inGameContainerRepository.save(this)
    }

    private fun randomizeItems(
        dbContainer: Container
    ): List<InventoryCell> {
        return if (!dbContainer.containerTags.isEmpty()) {
            randomItems(dbContainer.containerTags)
        } else {
            godModChest(dbContainer)
        }
    }

    private fun randomItems(
        containerTags: Set<ContainerTag>
    ): List<InventoryCell> {
        return lootTableHandler.generateLoot(containerTags)
    }

    private fun godModChest(dbContainer: Container): List<InventoryCell> {
        val num = (dbContainer.id ?: 0) % 9
        return when (num) {
            0L -> mapOfType(LOOT)
            1L -> mapOfType(RARE_LOOT)
            2L -> mapOfType(CULTIST_LOOT)
            3L -> mapOfType(INVESTIGATION)
            4L -> mapOfType(USEFUL_ITEM)
            5L -> mapOfType(CULTIST_ITEM)
            6L -> mapOfType(ADVANCED_USEFUL_ITEM)
            7L -> mapOfType(ADVANCED_CULTIST_ITEM)
            else -> mapOfType(TECH_TYPE)
        }
    }

    private fun mapOfType(type: ItemType) =
        Item.values().filter { it.itemType == type }.map {
            InventoryCell(it, 50)
        }

}