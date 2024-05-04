package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler

import com.arkhamusserver.arkhamus.logic.ingame.logic.CanAbilityBeCastedHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.UserInventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.EventVisibilityFilter
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.ErrorGameResponse
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.HeartbeatGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.RequestProcessData
import com.arkhamusserver.arkhamus.view.dto.netty.request.HeartbeatRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import org.springframework.stereotype.Component

@Component
class HeartbeatNettyRequestHandler(
    private val eventVisibilityFilter: EventVisibilityFilter,
    private val canAbilityBeCastedHandler: CanAbilityBeCastedHandler,
    private val inventoryHandler: UserInventoryHandler
) : NettyRequestHandler {

    override fun acceptClass(nettyRequestMessage: NettyBaseRequestMessage): Boolean =
        nettyRequestMessage::class.java == HeartbeatRequestMessage::class.java

    override fun accept(nettyRequestMessage: NettyBaseRequestMessage): Boolean = true

    override fun buildData(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ): RequestProcessData {
        val userId = requestDataHolder.userAccount.id
        requestDataHolder.gameSession?.id?.let {
            val user = globalGameData.users[userId]!!
            val users = globalGameData.users.values.filter { it.userId != userId }
            val visibleOngoingEvents = eventVisibilityFilter.filter(user, ongoingEvents)
            return HeartbeatGameData(
                gameUser = user,
                otherGameUsers = users,
                visibleOngoingEvents = visibleOngoingEvents,
                availableAbilities = canAbilityBeCastedHandler.abilityOfUserResponses(user, globalGameData),
                visibleItems = inventoryHandler.mapUsersItems(user.items),
                tick = globalGameData.game.currentTick,
            )
        } ?: return ErrorGameResponse("game session id is null", globalGameData.game.currentTick)
    }

}