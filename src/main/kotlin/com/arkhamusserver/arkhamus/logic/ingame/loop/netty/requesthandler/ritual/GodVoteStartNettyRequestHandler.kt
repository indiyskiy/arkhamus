package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.ritual

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.*
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.EventVisibilityFilter
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.ritual.GodVoteStartRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.NettyRequestHandler
import com.arkhamusserver.arkhamus.model.enums.ingame.God
import com.arkhamusserver.arkhamus.view.dto.netty.request.ritual.GodVoteStartRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import org.springframework.stereotype.Component

@Component
class GodVoteStartNettyRequestHandler(
    private val eventVisibilityFilter: EventVisibilityFilter,
    private val canAbilityBeCastedHandler: CanAbilityBeCastedHandler,
    private val inventoryHandler: InventoryHandler,
    private val crafterProcessHandler: CrafterProcessHandler,
    private val godVoteHandler: GodVoteHandler,
    private val zonesHandler: ZonesHandler,
    private val clueHandler: ClueHandler,
) : NettyRequestHandler {

    override fun acceptClass(nettyRequestMessage: NettyBaseRequestMessage): Boolean =
        nettyRequestMessage::class.java == GodVoteStartRequestMessage::class.java

    override fun accept(nettyRequestMessage: NettyBaseRequestMessage): Boolean = true

    override fun buildData(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ): GodVoteStartRequestProcessData {
        val request = requestDataHolder.nettyRequestMessage
        with(request as GodVoteStartRequestMessage) {
            val inZones = zonesHandler.filterByUserPosition(
                requestDataHolder.nettyRequestMessage.baseRequestData.userPosition,
                globalGameData.levelGeometryData
            )
            val userId = requestDataHolder.userAccount.id
            val user = globalGameData.users[userId]!!
            val users = globalGameData.users.values.filter { it.userId != userId }
            val altarHolder = globalGameData.altarHolder
            val altar = globalGameData.altars[this.altarId]
            val canBeStarted = godVoteHandler.canBeStarted(altarHolder, altar, ongoingEvents)
            val clues = clueHandler.filterClues(
                globalGameData.clues,
                inZones,
                globalGameData.castedAbilities,
                userId!!
            )
            return GodVoteStartRequestProcessData(
                votedGod = this.godId.toGod(),
                altar = altar,
                canBeStarted = canBeStarted,
                executedSuccessfully = false,
                gameUser = user,
                otherGameUsers = users,
                inZones = inZones,
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
                clues = clues
            )
        }
    }




    private fun Int.toGod(): God? = God.values().firstOrNull { it.getId() == this }
}
