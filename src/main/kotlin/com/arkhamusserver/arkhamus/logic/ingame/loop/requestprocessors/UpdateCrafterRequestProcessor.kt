package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.UpdateCrafterGameData
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisCrafterRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.MapObjectState
import com.arkhamusserver.arkhamus.model.redis.RedisCrafter
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.request.UpdateCrafterRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.ContainerCell
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.math.min

@Component
class UpdateCrafterRequestProcessor(
    private val redisCrafterRepository: RedisCrafterRepository,
) : NettyRequestProcessor {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(UpdateCrafterRequestProcessor::class.java)
    }

    override fun accept(request: NettyTickRequestMessageDataHolder): Boolean {
        return request.nettyRequestMessage is UpdateCrafterRequestMessage
    }

    override fun process(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        val requestProcessData = requestDataHolder.requestProcessData as UpdateCrafterGameData

        val updateCrafterRequestMessage = requestDataHolder.nettyRequestMessage as UpdateCrafterRequestMessage
        val oldGameUser = globalGameData.users[requestDataHolder.userAccount.id]!!
        val crafter = globalGameData.crafters[updateCrafterRequestMessage.crafterId]!!

        if ((crafter.state == MapObjectState.HOLD) && (crafter.holdingUser == oldGameUser.userId)) {
            val sortedInventory =
                getTrueNewInventoryContent(crafter, oldGameUser, updateCrafterRequestMessage.newInventoryContent)
            closeCrafter(crafter)
            requestProcessData.sortedInventory = sortedInventory
            requestProcessData.visibleItems = sortedInventory
        }
    }


    private fun getTrueNewInventoryContent(
        oldCrafter: RedisCrafter,
        oldGameUser: RedisGameUser,
        newInventoryContent: List<ContainerCell>
    ): List<ContainerCell> {
        val oldCrafterItems: List<Int> = oldCrafter.items.toList().filter { it.second > 0 }.map { it.first }
        val oldGameUserItems: List<Int> = oldGameUser.items.toList().filter { it.second > 0 }.map { it.first }
        val differentItemTypes = (oldCrafterItems + oldGameUserItems).distinct()
        val summarizedItems: MutableMap<Int, Long> = differentItemTypes.associateWith {
            ((oldCrafter.items[it] ?: 0) + (oldGameUser.items[it] ?: 0))
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
        oldCrafter.items = summarizedItems
            .filterNot { it.key == Item.PURE_NOTHING.id || it.value <= 0 }
            .toMap()
            .toMutableMap()
        oldGameUser.items =
            trueNewInventoryContent
                .filterNot { it.itemId == Item.PURE_NOTHING.id || it.number <= 0 }
                .groupBy { it.itemId }
                .map { it.key to it.value.sumOf { it.number } }
                .toMap()
                .toMutableMap()
        return trueNewInventoryContent
    }

    private fun closeCrafter(crafter: RedisCrafter) {
        crafter.holdingUser = null
        crafter.state = MapObjectState.ACTIVE
        redisCrafterRepository.save(crafter)
    }
}