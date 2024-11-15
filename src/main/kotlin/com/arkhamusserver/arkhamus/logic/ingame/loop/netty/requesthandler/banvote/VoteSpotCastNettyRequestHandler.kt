package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.banvote

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ClueHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserVoteHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ability.CanAbilityBeCastHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.craft.CrafterProcessHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest.QuestProgressHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ZonesHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.EventVisibilityFilter
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.banvote.VoteSpotCastRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.NettyRequestHandler
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.VoteSpotState
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisUserVoteSpot
import com.arkhamusserver.arkhamus.model.redis.RedisVoteSpot
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.banvote.VoteSpotCastRequestMessage
import org.springframework.stereotype.Component

@Component
class VoteSpotCastNettyRequestHandler(
    private val eventVisibilityFilter: EventVisibilityFilter,
    private val canAbilityBeCastHandler: CanAbilityBeCastHandler,
    private val inventoryHandler: InventoryHandler,
    private val crafterProcessHandler: CrafterProcessHandler,
    private val zonesHandler: ZonesHandler,
    private val clueHandler: ClueHandler,
    private val questProgressHandler: QuestProgressHandler,
    private val voteHandler: UserVoteHandler,
) : NettyRequestHandler {

    override fun acceptClass(nettyRequestMessage: NettyBaseRequestMessage): Boolean =
        nettyRequestMessage::class.java == VoteSpotCastRequestMessage::class.java

    override fun accept(nettyRequestMessage: NettyBaseRequestMessage): Boolean = true

    override fun buildData(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ): VoteSpotCastRequestProcessData {
        val request = requestDataHolder.nettyRequestMessage
        with(request as VoteSpotCastRequestMessage) {
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
                user
            )

            val voteSpot = globalGameData.voteSpots.firstOrNull { it.voteSpotId == voteSpotId }
            val thisSpotUserInfos = voteSpot?.let {
                globalGameData.userVoteSpotsBySpotId[voteSpotId]
            } ?: emptyList()
            val currentUserVoteSpot = thisSpotUserInfos.let {
                it.firstOrNull { it.userId == userId }
            }
            val targetUser = globalGameData.users[targetUserId]

            val canVote = canUserCastVote(currentUserVoteSpot, voteSpot, targetUser)
            val mastPay = voteSpot?.voteSpotState == VoteSpotState.WAITING_FOR_PAYMENT


            val canVoteForTargetUser = canVote &&
                    !mastPay &&
                    targetUser != null &&
                    currentUserVoteSpot != null

            return VoteSpotCastRequestProcessData(
                canVoteForTargetUser = canVoteForTargetUser,
                targetUserBanned = false,
                successfullyVoted = false,
                voteSpot = voteSpot,
                currentUserVoteSpot = currentUserVoteSpot,
                thisSpotUserInfos = thisSpotUserInfos,
                canVote = canVote,
                targetUser = targetUser,
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


    private fun canUserCastVote(
        userVoteSpot: RedisUserVoteSpot?,
        voteSpot: RedisVoteSpot?,
        targetUser: RedisGameUser?
    ): Boolean {
        if (voteSpot == null || userVoteSpot == null || targetUser == null) return false
        return voteHandler.canVote(userVoteSpot, targetUser, voteSpot)
    }

}



