package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.CanAbilityBeCastedHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.CrafterProcessHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.GodVoteHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.EventVisibilityFilter
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AltarOpenRequestProcessData
import com.arkhamusserver.arkhamus.view.dto.netty.request.AltarOpenRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import org.springframework.stereotype.Component

@Component
class AltarOpenNettyRequestHandler(
    private val eventVisibilityFilter: EventVisibilityFilter,
    private val canAbilityBeCastedHandler: CanAbilityBeCastedHandler,
    private val inventoryHandler: InventoryHandler,
    private val crafterProcessHandler: CrafterProcessHandler,
    private val godVoteHandler: GodVoteHandler
) : NettyRequestHandler {

    override fun acceptClass(nettyRequestMessage: NettyBaseRequestMessage): Boolean =
        nettyRequestMessage::class.java == AltarOpenRequestMessage::class.java

    override fun accept(nettyRequestMessage: NettyBaseRequestMessage): Boolean = true

    override fun buildData(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ): AltarOpenRequestProcessData {
        val request = requestDataHolder.nettyRequestMessage
        with(request as AltarOpenRequestMessage) {
            val userId = requestDataHolder.userAccount.id
            val user = globalGameData.users[userId]!!
            val users = globalGameData.users.values.filter { it.userId != userId }
            val altarHolder = globalGameData.altarHolder
            val altarPolling = globalGameData.altarPolling
            val altar = globalGameData.altars[request.altarId]
            return AltarOpenRequestProcessData(
                altar = altar,
                altarPolling = altarPolling,
                altarHolder = altarHolder,
                voteProcessOpen = godVoteHandler.isVoteProcessOpen(altarPolling, altarHolder),
                canVote = godVoteHandler.canVote(altarPolling, altarHolder, requestDataHolder.userAccount),
                canStartVote = godVoteHandler.canBeStarted(altarHolder, altar, ongoingEvents),
                gameUser = user,
                otherGameUsers = users,
                visibleOngoingEvents = eventVisibilityFilter.filter(user, ongoingEvents),
                availableAbilities = canAbilityBeCastedHandler.abilityOfUserResponses(user, globalGameData),
                visibleItems = inventoryHandler.mapUsersItems(user.items),
                ongoingCraftingProcess = crafterProcessHandler.filterAndMap(
                    user,
                    globalGameData.crafters,
                    globalGameData.craftProcess
                ),
                containers = globalGameData.containers.values.toList(),
                tick = globalGameData.game.currentTick
            )
        }
    }

}
