package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.UpdateContainerGameData
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisContainerRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.MapObjectState
import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.request.UpdateContainerRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.ContainerCell
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.math.min

@Component
class UpdateContainerRequestProcessor(
    private val redisContainerRepository: RedisContainerRepository,
) : NettyRequestProcessor {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(UpdateContainerRequestProcessor::class.java)
    }

    override fun accept(request: NettyTickRequestMessageDataHolder): Boolean {
        return request.nettyRequestMessage is UpdateContainerRequestMessage
    }

    override fun process(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        val requestProcessData = requestDataHolder.requestProcessData as UpdateContainerGameData

        val updateContainerRequestMessage = requestDataHolder.nettyRequestMessage as UpdateContainerRequestMessage
        val oldGameUser = globalGameData.users[requestDataHolder.userAccount.id]!!
        val container = globalGameData.containers[updateContainerRequestMessage.containerId]!!

        if ((container.state == MapObjectState.HOLD) && (container.holdingUser == oldGameUser.userId)) {
            val sortedInventory =
                getTrueNewInventoryContent(container, oldGameUser, updateContainerRequestMessage.newInventoryContent)
            if (updateContainerRequestMessage.close) {
                closeContainer(container)
            }
            requestProcessData.sortedInventory = sortedInventory
            requestProcessData.visibleItems = sortedInventory
        }
    }


    private fun getTrueNewInventoryContent(
        oldContainer: RedisContainer,
        oldGameUser: RedisGameUser,
        newInventoryContent: List<ContainerCell>
    ): List<ContainerCell> {
        val oldContainerItems: List<Int> = oldContainer.items.toList().filter { it.second > 0 }.map { it.first }
        val oldGameUserItems: List<Int> = oldGameUser.items.toList().filter { it.second > 0 }.map { it.first }
        val differentItemTypes = (oldContainerItems + oldGameUserItems).distinct()
        val summarizedItems: MutableMap<Int, Long> = differentItemTypes.associateWith {
            ((oldContainer.items[it] ?: 0) + (oldGameUser.items[it] ?: 0))
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
        oldContainer.items = summarizedItems
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

    private fun closeContainer(container: RedisContainer) {
        container.holdingUser = null
        container.state = MapObjectState.ACTIVE
        redisContainerRepository.save(container)
    }
}