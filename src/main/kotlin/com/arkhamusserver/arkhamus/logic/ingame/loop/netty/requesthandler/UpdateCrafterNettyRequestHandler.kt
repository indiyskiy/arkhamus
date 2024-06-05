package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.CanAbilityBeCastedHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.CrafterProcessHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.EventVisibilityFilter
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.RequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.UpdateCrafterRequestGameData
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.UpdateCrafterRequestMessage
import org.springframework.stereotype.Component

@Component
class UpdateCrafterNettyRequestHandler(
    private val eventVisibilityFilter: EventVisibilityFilter,
    private val canAbilityBeCastedHandler: CanAbilityBeCastedHandler,
    private val inventoryHandler: InventoryHandler,
    private val crafterProcessHandler: CrafterProcessHandler
) : NettyRequestHandler {

    override fun acceptClass(nettyRequestMessage: NettyBaseRequestMessage): Boolean =
        nettyRequestMessage::class.java == UpdateCrafterRequestMessage::class.java

    override fun accept(nettyRequestMessage: NettyBaseRequestMessage): Boolean = true

    override fun buildData(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ): RequestProcessData {
        val userId = requestDataHolder.userAccount.id
        val request = requestDataHolder.nettyRequestMessage
        with(request as UpdateCrafterRequestMessage) {
            val crafter = globalGameData.crafters[this.externalInventoryId]!!
            val user = globalGameData.users[userId]!!
            val users = globalGameData.users.values.filter { it.userId != userId }
            val sortedUserInventory = request.newInventoryContent
            return UpdateCrafterRequestGameData(
                crafter = crafter,
                gameUser = user,
                otherGameUsers = users,
                executedSuccessfully = true,
                sortedUserInventory = sortedUserInventory,
                visibleOngoingEvents = eventVisibilityFilter.filter(user, ongoingEvents),
                availableAbilities = canAbilityBeCastedHandler.abilityOfUserResponses(user, globalGameData),
                visibleItems = inventoryHandler.mapUsersItems(user.items),
                ongoingCraftingProcess = crafterProcessHandler.filterAndMap(
                    user,
                    globalGameData.crafters,
                    globalGameData.craftProcess
                ),
                containers = globalGameData.containers.values.toList(),
                tick = globalGameData.game.currentTick,
            )
        }
    }
}