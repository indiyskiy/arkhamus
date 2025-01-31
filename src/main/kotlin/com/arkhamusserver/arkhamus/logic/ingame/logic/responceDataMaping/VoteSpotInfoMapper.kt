package com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping

import com.arkhamusserver.arkhamus.model.enums.ingame.BanState
import com.arkhamusserver.arkhamus.model.ingame.InGameUserVoteSpot
import com.arkhamusserver.arkhamus.model.ingame.InGameVoteSpot
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.UserWithBanState
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.VoteSpotInfo
import org.springframework.stereotype.Component

@Component
class VoteSpotInfoMapper() {
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

}