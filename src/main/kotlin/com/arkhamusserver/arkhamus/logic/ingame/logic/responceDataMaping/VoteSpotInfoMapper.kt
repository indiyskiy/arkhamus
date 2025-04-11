package com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.LevelGeometryData
import com.arkhamusserver.arkhamus.model.enums.ingame.BanState
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapObjectState
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.VoteSpotState
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.InGameUserVoteSpot
import com.arkhamusserver.arkhamus.model.ingame.InGameVoteSpot
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.EasyVoteSpotResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.UserWithBanState
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.VoteSpotInfo
import org.springframework.stereotype.Component

@Component
class VoteSpotInfoMapper(
    private val userLocationHandler: UserLocationHandler,
) {
    fun map(
        voteSpot: InGameVoteSpot?,
        currentUserVoteSpot: InGameUserVoteSpot?,
        thisSpotUserInfos: List<InGameUserVoteSpot>,
        allUserIds: List<Long>,
    ): VoteSpotInfo? =
        voteSpot?.let { voteSpotNotNull ->
            val costValue = voteSpotNotNull.costValue
            val costItem = voteSpotNotNull.costItem
            val state = voteSpotNotNull.voteSpotState
            val usersWithBanStates = allUserIds.map { targetUserId ->
                mapUserWithBanState(
                    targetUserId = targetUserId,
                    voteSpotNotNull = voteSpotNotNull,
                    currentUserVoteSpot = currentUserVoteSpot,
                    thisSpotUserInfos = thisSpotUserInfos
                )
            }
            VoteSpotInfo(costValue, costItem).apply {
                this.state = state
                this.usersWithBanStates = usersWithBanStates
            }
        }


    private fun mapUserWithBanState(
        targetUserId: Long,
        voteSpotNotNull: InGameVoteSpot,
        currentUserVoteSpot: InGameUserVoteSpot?,
        thisSpotUserInfos: List<InGameUserVoteSpot>
    ): UserWithBanState =
        UserWithBanState(
            userId = targetUserId,
            banState = banState(targetUserId, voteSpotNotNull.bannedUsers, voteSpotNotNull),
            voteCount = countVotes(targetUserId, thisSpotUserInfos),
            currentUserVoteCast = currentUserVoteSpot?.votesForUserIds?.any { it == targetUserId } == true
        )

    private fun countVotes(
        userId: Long,
        spots: List<InGameUserVoteSpot>
    ): Int =
        spots.count { it.votesForUserIds.contains(userId) }


    private fun banState(
        userId: Long,
        bannedUsers: List<Long>,
        voteSpot: InGameVoteSpot,
    ): BanState =
        if (bannedUsers.any { it == userId }) {
            BanState.BANNED
        } else {
            if (voteSpot.availableUsers.contains(userId)) {
                BanState.AVAILABLE_FOR_VOTING
            } else {
                BanState.NOT_AVAILABLE_FOR_VOTING
            }
        }

    fun mapEasy(
        user: InGameUser,
        spots: List<InGameVoteSpot>,
        data: LevelGeometryData
    ): List<EasyVoteSpotResponse> {
        return spots.map {
            val canSee = userLocationHandler.userCanSeeTarget(user, it, data, true)
            val state = mapState(canSee)
            val voteSpotState = mapVoteSpotState(it, state)
            EasyVoteSpotResponse(
                voteSpotId = it.inGameId(),
                state = state,
                voteSpotState = voteSpotState
            )
        }
    }

    private fun mapState(
        canSee: Boolean
    ): MapObjectState {
        return if (!canSee) {
            MapObjectState.NOT_IN_SIGHT
        } else {
            MapObjectState.ACTIVE
        }
    }

    private fun mapVoteSpotState(
        spot: InGameVoteSpot,
        state: MapObjectState,
    ): VoteSpotState {
        return if (state == MapObjectState.NOT_IN_SIGHT) {
            VoteSpotState.WAITING_FOR_PAYMENT
        } else {
            spot.voteSpotState
        }
    }

}