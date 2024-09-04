package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.banvote

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.*
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest.QuestProgressHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.EventVisibilityFilter
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.banvote.VoteSpotOpenRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.NettyRequestHandler
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.banvote.VoteSpotOpenRequestMessage
import org.springframework.stereotype.Component

@Component
class VoteSpotOpenNettyRequestHandler(
    private val eventVisibilityFilter: EventVisibilityFilter,
    private val canAbilityBeCastHandler: CanAbilityBeCastHandler,
    private val inventoryHandler: InventoryHandler,
    private val crafterProcessHandler: CrafterProcessHandler,
    private val zonesHandler: ZonesHandler,
    private val clueHandler: ClueHandler,
    private val questProgressHandler: QuestProgressHandler,
    private val madnessHandler: UserMadnessHandler,
) : NettyRequestHandler {

    override fun acceptClass(nettyRequestMessage: NettyBaseRequestMessage): Boolean =
        nettyRequestMessage::class.java == VoteSpotOpenRequestMessage::class.java

    override fun accept(nettyRequestMessage: NettyBaseRequestMessage): Boolean = true

    override fun buildData(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ): VoteSpotOpenRequestProcessData {
        val request = requestDataHolder.nettyRequestMessage
        with(request as VoteSpotOpenRequestMessage) {
            val inZones = zonesHandler.filterByPosition(
                requestDataHolder.nettyRequestMessage.baseRequestData.userPosition,
                globalGameData.levelGeometryData
            )
            val userId = requestDataHolder.userAccount.id
            val user = globalGameData.users[userId]!!
            val users = globalGameData.users.values.filter { it.userId != userId }

            val clues = clueHandler.filterClues(
                globalGameData.clues,
                inZones,
                globalGameData.castAbilities,
                userId!!
            )

            val voteSpot = globalGameData.voteSpots.firstOrNull { it.voteSpotId == voteSpotId }
            val thisSpotUserInfos = voteSpot?.let {
                globalGameData.userVoteSpotsBySpotId[voteSpotId]
            } ?: emptyList()
            val currentUserVoteSpot = thisSpotUserInfos.let {
                it.firstOrNull { it.userId == userId }
            }
            val currentUserBanned = voteSpot?.bannedUsers?.any { it == userId } == true
            val canVote = !madnessHandler.isCompletelyMad(user) && !currentUserBanned
            val costValue = voteSpot?.costValue
            val canPay = costValue!=null && inventoryHandler.userHaveItems(
                user = user,
                requiredItemId = voteSpot.costItem,
                howManyItems = costValue
            )

            return VoteSpotOpenRequestProcessData(
                voteSpot = voteSpot,
                currentUserVoteSpot = currentUserVoteSpot,
                thisSpotUserInfos = thisSpotUserInfos,
                canVote = canVote,
                canPay = canPay,
                currentUserBanned = currentUserBanned,
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



