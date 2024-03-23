package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageContainer
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisContainerRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.MapObjectState
import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.request.CloseContainerRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.ContainerCell
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class CloseContainerRequestProcessor(
    private val redisContainerRepository: RedisContainerRepository
) : NettyRequestProcessor {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(CloseContainerRequestProcessor::class.java)
    }

    override fun accept(request: NettyTickRequestMessageContainer): Boolean {
        return request.nettyRequestMessage is CloseContainerRequestMessage
    }

    override fun process(
        request: NettyTickRequestMessageContainer,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        val closeContainerRequestMessage = request.nettyRequestMessage as CloseContainerRequestMessage
        val oldGameUser = globalGameData.users[request.userAccount.id]!!
        val container = globalGameData.containers[closeContainerRequestMessage.containerId]!!

        if ((container.state == MapObjectState.HOLD) && (container.holdingUser == oldGameUser.userId)) {
            takeItems(container, oldGameUser, closeContainerRequestMessage.newInventoryContent)
            closeContainer(container)
            sortItemsInInventory(closeContainerRequestMessage.newInventoryContent, oldGameUser)
        }
    }


    private fun takeItems(
        oldContainer: RedisContainer,
        oldGameUser: RedisGameUser,
        newInventoryContent: List<ContainerCell>
    ) {
        updateItemsByNewStateOfInventory(newInventoryContent, oldContainer, oldGameUser)
        updateItemsRemovedFromInventory(oldGameUser, oldContainer, newInventoryContent)
    }

    private fun updateItemsByNewStateOfInventory(
        newInventoryContent: List<ContainerCell>,
        oldContainer: RedisContainer,
        oldGameUser: RedisGameUser
    ) {
        newInventoryContent.forEach { newInventoryState ->
            if (newInventoryState.number < 0) {
                return
            }
            val fromOldContainer = oldContainer.items[newInventoryState.itemId] ?: 0
            val fromOldUserInventory = oldContainer.items[newInventoryState.itemId] ?: 0
            val itemsToPutInContainer = fromOldUserInventory - newInventoryState.number
            if (itemsToPutInContainer >= 0) {
                //put to container {itemsToPutInContainer} items
                oldContainer.items[newInventoryState.itemId] = fromOldContainer + itemsToPutInContainer
                oldGameUser.items[newInventoryState.itemId] = newInventoryState.number
            } else {
                //take from container {itemsToPutInContainer} items
                val newItemsInContainer = fromOldContainer + itemsToPutInContainer
                if (newItemsInContainer >= 0) {
                    oldContainer.items[newInventoryState.itemId] = newItemsInContainer
                    oldGameUser.items[newInventoryState.itemId] = newInventoryState.number
                }
            }
        }
    }

    private fun updateItemsRemovedFromInventory(
        oldInventory: RedisGameUser,
        oldContainer: RedisContainer,
        newInventoryState: List<ContainerCell>,
    ) {
        oldInventory.items.forEach { (oldInventoryItemId, oldInventoryItemsNumber) ->
            val newStateOfInventory = newInventoryState.firstOrNull { it.itemId == oldInventoryItemId }
            if (newStateOfInventory != null) {
                if (newStateOfInventory.number != oldInventoryItemsNumber) {
                    logger.warn("strange inventory behaviour!")
                }
            } else {
                oldInventory.items[oldInventoryItemId] = 0
                oldContainer.items[oldInventoryItemId] =
                    (oldContainer.items[oldInventoryItemId] ?: 0) + oldInventoryItemsNumber
            }
        }
    }

    private fun sortItemsInInventory(newInventoryContent: List<ContainerCell>, oldGameUser: RedisGameUser) {
        val sortedInventory = newInventoryContent.map {
            val realNumber = oldGameUser.items[it.itemId] ?: 0
            if (realNumber > 0 && it.itemId != Item.PURE_NOTHING.getId()) {
                it.itemId to realNumber
            } else {
                Item.PURE_NOTHING.getId() to 0L
            }
        }
        oldGameUser.items = sortedInventory.toMap().toMutableMap()
    }

    private fun closeContainer(container: RedisContainer) {
        container.holdingUser = null
        container.state = MapObjectState.ACTIVE
        redisContainerRepository.save(container)
    }
}