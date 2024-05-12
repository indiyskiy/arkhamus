package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler

import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import com.arkhamusserver.arkhamus.model.redis.RedisCrafter
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.ContainerCell
import org.springframework.stereotype.Component
import kotlin.math.min

@Component
class ContainerTypeThingsHandler {
    fun getTrueNewInventoryContent(
        oldCrafter: RedisCrafter,
        oldGameUser: RedisGameUser,
        newInventoryContent: List<ContainerCell>
    ): List<ContainerCell> {
        val oldCrafterItemsList = oldCrafter.items
        val oldGameUserItemsList = oldGameUser.items

        val (summarizedItems: MutableMap<Int, Long>, trueNewInventoryContent: List<ContainerCell>) = calculateInventory(
            oldCrafterItemsList,
            oldGameUserItemsList,
            oldGameUser,
            newInventoryContent
        )

        oldCrafter.items = summarizedItems
            .filterNot { it.key == Item.PURE_NOTHING.id || it.value <= 0 }
            .toMap()
            .toMutableMap()
        oldGameUser.items = mapSimpleInventory(trueNewInventoryContent)
        return trueNewInventoryContent
    }

    fun getTrueNewInventoryContent(
        oldContainer: RedisContainer,
        oldGameUser: RedisGameUser,
        newInventoryContent: List<ContainerCell>
    ): List<ContainerCell> {
        val oldContainerItemsList = oldContainer.items
        val oldGameUserItemsList = oldGameUser.items

        val (summarizedItems: MutableMap<Int, Long>, trueNewInventoryContent: List<ContainerCell>) = calculateInventory(
            oldContainerItemsList,
            oldGameUserItemsList,
            oldGameUser,
            newInventoryContent
        )

        oldContainer.items = summarizedItems
            .filterNot { it.key == Item.PURE_NOTHING.id || it.value <= 0 }
            .toMap()
            .toMutableMap()

        oldGameUser.items = mapSimpleInventory(trueNewInventoryContent)
        return trueNewInventoryContent
    }

    private fun mapSimpleInventory(trueNewInventoryContent: List<ContainerCell>) =
        trueNewInventoryContent
            .filterNot { it.itemId == Item.PURE_NOTHING.id || it.number <= 0 }
            .groupBy { it.itemId }
            .map { it.key to it.value.sumOf { it.number } }
            .toMap()
            .toMutableMap()

    private fun calculateInventory(
        oldContainerItemsList: MutableMap<Int, Long>,
        oldGameUserItemsList: MutableMap<Int, Long>,
        oldGameUser: RedisGameUser,
        newInventoryContent: List<ContainerCell>
    ): Pair<MutableMap<Int, Long>, List<ContainerCell>> {
        val oldContainerItems: List<Int> = oldContainerItemsList.toList().filter { it.second > 0 }.map { it.first }
        val oldGameUserItems: List<Int> = oldGameUserItemsList.toList().filter { it.second > 0 }.map { it.first }
        val differentItemTypes = (oldContainerItems + oldGameUserItems).distinct()
        val summarizedItems: MutableMap<Int, Long> = differentItemTypes.associateWith {
            ((oldContainerItemsList[it] ?: 0) + (oldGameUser.items[it] ?: 0))
        }.toMutableMap()
        val trueNewInventoryContent: List<ContainerCell> = newInventoryContent.map {
            val itemId = it.itemId
            val newValue = min(it.number, summarizedItems[itemId] ?: 0)
            if (newValue > 0) {
                summarizedItems[itemId] = (summarizedItems[itemId] ?: 0) - newValue
                ContainerCell(itemId, newValue)
            } else {
                ContainerCell(Item.PURE_NOTHING.id, 0)
            }
        }
        return Pair(summarizedItems, trueNewInventoryContent)
    }
}