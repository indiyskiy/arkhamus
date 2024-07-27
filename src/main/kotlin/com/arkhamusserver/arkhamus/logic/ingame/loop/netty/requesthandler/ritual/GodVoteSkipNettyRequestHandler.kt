package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.ritual

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.*
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest.QuestProgressHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.EventVisibilityFilter
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.ritual.GodVoteSkipRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.NettyRequestHandler
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.ritual.GodVoteSkipRequestMessage
import org.springframework.stereotype.Component

@Component
class GodVoteSkipNettyRequestHandler(
    private val eventVisibilityFilter: EventVisibilityFilter,
    private val canAbilityBeCastHandler: CanAbilityBeCastHandler,
    private val inventoryHandler: InventoryHandler,
    private val crafterProcessHandler: CrafterProcessHandler,
    private val godVoteHandler: GodVoteHandler,
    private val zonesHandler: ZonesHandler,
    private val clueHandler: ClueHandler,
    private val questProgressHandler: QuestProgressHandler,
) : NettyRequestHandler {

    override fun acceptClass(nettyRequestMessage: NettyBaseRequestMessage): Boolean =
        nettyRequestMessage::class.java == GodVoteSkipRequestMessage::class.java

    override fun accept(nettyRequestMessage: NettyBaseRequestMessage): Boolean = true

    override fun buildData(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ): GodVoteSkipRequestProcessData {
        val request = requestDataHolder.nettyRequestMessage
        with(request as GodVoteSkipRequestMessage) {
            val inZones = zonesHandler.filterByPosition(
                requestDataHolder.nettyRequestMessage.baseRequestData.userPosition,
                globalGameData.levelGeometryData
            )
            val userId = requestDataHolder.userAccount.id
            val user = globalGameData.users[userId]!!
            val users = globalGameData.users.values.filter { it.userId != userId }
            val altarHolder = globalGameData.altarHolder
            val altarPolling = globalGameData.altarPolling
            val altar = globalGameData.altars[this.altarId]
            val canSkip = godVoteHandler.canVote(altarPolling, altarHolder, user)
            val clues = clueHandler.filterClues(
                globalGameData.clues,
                inZones,
                globalGameData.castAbilities,
                userId!!
            )
            return GodVoteSkipRequestProcessData(
                altar = altar,
                canSkip = canSkip,
                executedSuccessfully = false,
                gameUser = user,
                otherGameUsers = users,
                inZones = inZones,
                visibleOngoingEvents = eventVisibilityFilter.filter(user, ongoingEvents),
                availableAbilities = canAbilityBeCastHandler.abilityOfUserResponses(user, globalGameData),
                visibleItems = inventoryHandler.mapUsersItems(user.items),
                ongoingCraftingProcess = crafterProcessHandler.filterAndMap(
                    user,
                    globalGameData.crafters,
                    globalGameData.craftProcess
                ),
                containers = globalGameData.containers.values.toList(),
                crafters = globalGameData.crafters.values.toList(),
                tick = globalGameData.game.currentTick,
                clues = clues,
                userQuestProgresses = questProgressHandler.mapQuestProgresses(
                    globalGameData.questProgressByUserId,
                    user,
                    globalGameData.quests
                ),
            )
        }
    }

}
