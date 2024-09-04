package com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.shortTime

import com.arkhamusserver.arkhamus.model.enums.ingame.BanState
import com.arkhamusserver.arkhamus.model.redis.RedisUserVoteSpot
import com.arkhamusserver.arkhamus.model.redis.RedisVoteSpot
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.UserWithBanState
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.VoteSpotInfo
import org.springframework.stereotype.Component

@Component
class VoteSpotInfoMapper() {
    fun map(
        voteSpot: RedisVoteSpot?,
        currentUserVoteSpot: RedisUserVoteSpot?,
        thisSpotUserInfos: List<RedisUserVoteSpot>,
        allUserIds: List<Long>,
    ): VoteSpotInfo? {
        voteSpot?.let { voteSpotNotNull ->
            val costValue = voteSpotNotNull.costValue
            val costItem = voteSpotNotNull.costItem
            val usersWithBanStates = allUserIds.map { targetUserId ->
                mapUserWithBanState(
                    targetUserId = targetUserId,
                    voteSpotNotNull = voteSpotNotNull,
                    currentUserVoteSpot = currentUserVoteSpot,
                    thisSpotUserInfos = thisSpotUserInfos
                )
            }
            return VoteSpotInfo().apply {
                this.costValue = costValue
                this.costItem = costItem
                this.usersWithBanStates = usersWithBanStates
            }
        }
        return null
    }

    private fun mapUserWithBanState(
        targetUserId: Long,
        voteSpotNotNull: RedisVoteSpot,
        currentUserVoteSpot: RedisUserVoteSpot?,
        thisSpotUserInfos: List<RedisUserVoteSpot>
    ): UserWithBanState {
        return UserWithBanState(
            userId = targetUserId,
            banState = banState(targetUserId, voteSpotNotNull.bannedUsers),
            voteCount = countVotes(targetUserId, thisSpotUserInfos),
            currentUserVoteCast = currentUserVoteSpot?.votesForUserIds?.any { it == targetUserId } == true
        )
    }

    private fun countVotes(
        userId: Long,
        spots: List<RedisUserVoteSpot>
    ): Int {
        return spots.count { it.votesForUserIds.contains(userId) }
    }

    private fun banState(
        userId: Long,
        bannedUsers: List<Long>,
    ): BanState {
        return if (bannedUsers.any { it == userId }) {
            BanState.BANNED
        } else {
            BanState.AVAILABLE_FOR_VOTING
        }
    }

}