package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.ritual

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.*
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.EventVisibilityFilter
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.ritual.AltarOpenRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.NettyRequestHandler
import com.arkhamusserver.arkhamus.model.enums.ingame.God
import com.arkhamusserver.arkhamus.model.enums.ingame.MapAltarPollingState
import com.arkhamusserver.arkhamus.model.redis.RedisAltarPolling
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.request.ritual.AltarOpenRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import org.springframework.stereotype.Component

@Component
class AltarOpenNettyRequestHandler(
    private val eventVisibilityFilter: EventVisibilityFilter,
    private val canAbilityBeCastHandler: CanAbilityBeCastHandler,
    private val inventoryHandler: InventoryHandler,
    private val crafterProcessHandler: CrafterProcessHandler,
    private val godVoteHandler: GodVoteHandler,
    private val zonesHandler: ZonesHandler,
    private val clueHandler: ClueHandler,
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
            val inZones = zonesHandler.filterByUserPosition(
                requestDataHolder.nettyRequestMessage.baseRequestData.userPosition,
                globalGameData.levelGeometryData
            )
            val userId = requestDataHolder.userAccount.id
            val user = globalGameData.users[userId]!!
            val users = globalGameData.users.values.filter { it.userId != userId }
            val altarHolder = globalGameData.altarHolder
            val altarPolling = globalGameData.altarPolling
            val altar = globalGameData.altars[this.altarId]
            val clues = clueHandler.filterClues(
                globalGameData.clues,
                inZones,
                globalGameData.castAbilities,
                userId!!
            )
            return AltarOpenRequestProcessData(
                altar = altar,
                altarPolling = altarPolling,
                altarHolder = altarHolder,
                voteState = altarPolling?.state ?: MapAltarPollingState.NOT_STARTED,
                votedForGod = votedForGod(altarPolling, user),
                godLocked = globalGameData.altarHolder.lockedGodId?.toGod(),
                voteProcessOpen = godVoteHandler.isVoteProcessOpen(altarPolling, altarHolder),
                canVote = godVoteHandler.canVote(altarPolling, altarHolder, user),
                canStartVote = godVoteHandler.canBeStarted(altarHolder, altar, ongoingEvents),
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
                tick = globalGameData.game.currentTick,
                clues = clues
            )
        }
    }

    private fun votedForGod(
        altarPolling: RedisAltarPolling?,
        user: RedisGameUser
    ) = altarPolling?.userVotes?.get(user.userId)?.toGod()

    private fun Int.toGod() =
        God.values().firstOrNull { it.getId() == this }
}



