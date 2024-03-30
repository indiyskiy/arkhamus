package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler

import com.arkhamusserver.arkhamus.logic.ingame.item.AbilityToItemResolver
import com.arkhamusserver.arkhamus.logic.ingame.logic.UserInventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.EventVisibilityFilter
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.RequestProcessData
import com.arkhamusserver.arkhamus.model.enums.ingame.Ability
import com.arkhamusserver.arkhamus.view.dto.netty.request.AbilityRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import org.springframework.stereotype.Component

@Component
class AbilityNettyRequestHandler(
    private val eventVisibilityFilter: EventVisibilityFilter,
    private val userInventoryHandler: UserInventoryHandler,
    private val abilityToItemResolver: AbilityToItemResolver
) : NettyRequestHandler {

    override fun acceptClass(nettyRequestMessage: NettyBaseRequestMessage): Boolean =
        nettyRequestMessage::class.java == AbilityRequestMessage::class.java

    override fun accept(nettyRequestMessage: NettyBaseRequestMessage): Boolean = true

    override fun buildData(
        nettyTickRequestMessageContainer: NettyTickRequestMessageContainer,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ): RequestProcessData {
        val userId = nettyTickRequestMessageContainer.userAccount.id
        val request = nettyTickRequestMessageContainer.nettyRequestMessage
        with(request as AbilityRequestMessage) {
            val ability = Ability.byId(request.abilityId)
            val user = globalGameData.users[userId]!!
            val users = globalGameData.users.values.filter { it.userId != userId }
            return ability?.let {
                val requiredItem = abilityToItemResolver.resolve(it)
                if (requiredItem != null) {
                    val haveItem = userInventoryHandler.userHaveItem(user, requiredItem)
                    AbilityRequestProcessData(
                        ability = ability,
                        canBeCasted = haveItem,
                        item = requiredItem,
                        castedSuccessfully = false,
                        gameUser = user,
                        otherGameUsers = users,
                        visibleOngoingEvents = eventVisibilityFilter.filter(user, ongoingEvents),
                        tick = globalGameData.game.currentTick
                    )
                } else {
                    AbilityRequestProcessData(
                        ability = ability,
                        canBeCasted = true,
                        item = null,
                        castedSuccessfully = false,
                        gameUser = user,
                        otherGameUsers = users,
                        visibleOngoingEvents = eventVisibilityFilter.filter(user, ongoingEvents),
                        tick = globalGameData.game.currentTick
                    )
                }
            } ?: AbilityRequestProcessData(
                ability = null,
                canBeCasted = false,
                item = null,
                castedSuccessfully = false,
                gameUser = user,
                otherGameUsers = users,
                visibleOngoingEvents = eventVisibilityFilter.filter(user, ongoingEvents),
                tick = globalGameData.game.currentTick
            )
        }
    }
}
