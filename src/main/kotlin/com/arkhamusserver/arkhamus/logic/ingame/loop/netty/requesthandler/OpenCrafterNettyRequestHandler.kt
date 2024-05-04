package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler

import com.arkhamusserver.arkhamus.logic.ingame.logic.CanAbilityBeCastedHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.UserInventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.EventVisibilityFilter
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.OpenCrafterGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.RequestProcessData
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.OpenCrafterRequestMessage
import org.springframework.stereotype.Component

@Component
class OpenCrafterNettyRequestHandler(
    private val eventVisibilityFilter: EventVisibilityFilter,
    private val canAbilityBeCastedHandler: CanAbilityBeCastedHandler,
    private val inventoryHandler: UserInventoryHandler
) : NettyRequestHandler {

    override fun acceptClass(nettyRequestMessage: NettyBaseRequestMessage): Boolean =
        nettyRequestMessage::class.java == OpenCrafterRequestMessage::class.java

    override fun accept(nettyRequestMessage: NettyBaseRequestMessage): Boolean = true

    override fun buildData(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>,
    ): RequestProcessData {
        val userId = requestDataHolder.userAccount.id
        val request = requestDataHolder.nettyRequestMessage
        with(request as OpenCrafterRequestMessage) {
            val crafter = globalGameData.crafters[this.crafterId]!!
            val user = globalGameData.users[userId]!!
            val users = globalGameData.users.values.filter { it.userId != userId }
            return OpenCrafterGameData(
                crafter = crafter,
                gameUser = user,
                otherGameUsers = users,
                visibleOngoingEvents = eventVisibilityFilter.filter(user, ongoingEvents),
                availableAbilities = canAbilityBeCastedHandler.abilityOfUserResponses(user, globalGameData),
                visibleItems = inventoryHandler.mapUsersItems(user.items),
                globalGameData.game.currentTick
            )
        }
    }
}