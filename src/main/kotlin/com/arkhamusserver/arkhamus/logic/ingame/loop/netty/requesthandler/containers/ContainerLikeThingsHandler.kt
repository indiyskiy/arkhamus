package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.containers

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InventoryHandler
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.ingame.InGameContainer
import com.arkhamusserver.arkhamus.model.ingame.InGameCrafter
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.InventoryCell
import org.springframework.stereotype.Component
import kotlin.math.min

@Component
class ContainerLikeThingsHandler(
    private val inventoryHandler: InventoryHandler
) {

    companion object {
        private val logger = LoggingUtils.getLogger<ContainerLikeThingsHandler>()
    }

    fun getTrueNewInventoryContent(
        oldCrafter: InGameCrafter,
        oldGameUser: InGameUser,
        newInventoryContent: List<InventoryCell>
    ): List<InventoryCell> {
        val oldCrafterItemsList = oldCrafter.items
        val oldGameUserItemsList = oldGameUser.additionalData.inventory.items

        val (summarizedItems: MutableMap<Item, Int>, trueNewInventoryContent: List<InventoryCell>) = calculateInventory(
            oldCrafterItemsList,
            oldGameUserItemsList,
            newInventoryContent
        )

        oldCrafter.items = summarizedItems
            .filterNot { it.key == Item.PURE_NOTHING || it.value <= 0 }
            .map { InventoryCell(it.key, it.value) }
        oldGameUser.additionalData.inventory.items = mapSimpleInventory(trueNewInventoryContent)
        return trueNewInventoryContent
    }

    fun getTrueNewInventoryContent(
        oldContainer: InGameContainer,
        oldGameUser: InGameUser,
        newInventoryContent: List<InventoryCell>
    ): List<InventoryCell> {
        logger.info("items from request = ${newInventoryContent.joinToString { it.item.name + " " + it.number }}")
        val oldContainerItemsList = oldContainer.items
        val oldGameUserItemsList = oldGameUser.additionalData.inventory.items

        val (summarizedItems: MutableMap<Item, Int>, trueNewInventoryContent: List<InventoryCell>) = calculateInventory(
            oldContainerItemsList,
            oldGameUserItemsList,
            newInventoryContent
        )

        oldContainer.items = summarizedItems
            .filter { it.key != Item.PURE_NOTHING && it.value > 0 }
            .map { InventoryCell(it.key, it.value) }
        logger.info("new container items = ${oldContainer.items.joinToString { it.item.name + " " + it.number }}")

        oldGameUser.additionalData.inventory.items = mapSimpleInventory(trueNewInventoryContent)
        logger.info("new user items = ${oldGameUser.additionalData.inventory.items.joinToString { it.item.name + " " + it.number }}")
        return trueNewInventoryContent
    }

    private fun mapSimpleInventory(trueNewInventoryContent: List<InventoryCell>) =
        trueNewInventoryContent
            .filterNot { it.item == Item.PURE_NOTHING || it.number <= 0 }
            .groupBy { it.item }
            .map { cell ->
                InventoryCell(cell.key, cell.value.sumOf { it.number })
            }


    private fun calculateInventory(
        oldContainerItemsList: List<InventoryCell>,
        oldGameUserItemsList: List<InventoryCell>,
        newInventoryContent: List<InventoryCell>
    ): Pair<MutableMap<Item, Int>, List<InventoryCell>> {
        val oldContainerItems: List<Item> = oldContainerItemsList.filter { it.number > 0 }.map { it.item }
        val oldGameUserItems: List<Item> = oldGameUserItemsList.filter { it.number > 0 }.map { it.item }

        val differentItemTypes = (oldContainerItems + oldGameUserItems).distinct()

        val summarizedItems: MutableMap<Item, Int> = differentItemTypes.associateWith {
            inventoryHandler.howManyItems(oldContainerItemsList, it) + inventoryHandler.howManyItems(
                oldGameUserItemsList,
                it
            )
        }.toMutableMap()
        val trueNewInventoryContent: List<InventoryCell> = newInventoryContent.map { cell ->
            val item = cell.item
            val newValue = min(cell.number, summarizedItems[item] ?: 0)
            if (newValue > 0) {
                summarizedItems[item] = (summarizedItems[item] ?: 0) - newValue
                InventoryCell(item, newValue)
            } else {
                InventoryCell(Item.PURE_NOTHING, 0)
            }
        }
        return Pair(summarizedItems, trueNewInventoryContent)
    }
}