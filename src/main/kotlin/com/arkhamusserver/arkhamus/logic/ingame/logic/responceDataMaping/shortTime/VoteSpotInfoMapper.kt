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
        currentUserId: Long,
        voteSpot: RedisVoteSpot?,
        currentUserVoteSpot: RedisUserVoteSpot?,
        thisSpotUserInfos: List<RedisUserVoteSpot>,
        allUserIds: List<Long>,
    ): VoteSpotInfo? {
        voteSpot?.let { voteSpotNotNull ->
            val costValue = voteSpotNotNull.costValue
            val costItem = voteSpotNotNull.costItem
            val usersWithBanStates = allUserIds.map { userId ->
                mapUserWithBanState(
                    currentUserId = currentUserId,
                    userId = userId,
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
        currentUserId: Long,
        userId: Long,
        voteSpotNotNull: RedisVoteSpot,
        currentUserVoteSpot: RedisUserVoteSpot?,
        thisSpotUserInfos: List<RedisUserVoteSpot>
    ): UserWithBanState {
        return UserWithBanState(
            userId = userId,
            banState = banState(userId, voteSpotNotNull.bannedUsers, currentUserId),
            voteCount = countVotes(userId, thisSpotUserInfos),
            currentUserVoteCast = currentUserVoteSpot?.votesForUserIds?.any{it == userId} == true
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
        currentUserId: Long
    ): BanState {
        return if (bannedUsers.any { it == userId }) {
            BanState.BANNED
        } else {
            if (userId == currentUserId) {
                BanState.NOT_AVAILABLE_FOR_VOTING
            } else {
                BanState.AVAILABLE_FOR_VOTING
            }
        }
    }

}