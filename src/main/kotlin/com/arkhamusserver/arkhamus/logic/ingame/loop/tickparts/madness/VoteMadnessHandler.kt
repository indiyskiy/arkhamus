package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.madness

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserVoteHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisVoteSpotRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.VoteSpotState
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisUserVoteSpot
import com.arkhamusserver.arkhamus.model.redis.RedisVoteSpot
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class VoteMadnessHandler(
    private val userLocationHandler: UserLocationHandler,
    private val inventoryHandler: InventoryHandler,
    private val voteSpotRedisVoteSpot: RedisVoteSpotRepository,
    private val userVoteHandler: UserVoteHandler,
) {

    companion object {
        private val random = Random(System.currentTimeMillis())
    }

    fun voteForSomeone(
        user: RedisGameUser,
        data: GlobalGameData,
        timePassedMillis: Long
    ): Boolean {
        val voteSpots = voteSpots(data, user)
        if (voteSpots.isNotEmpty()) {
            val voteSpot = voteSpots.random(random)
            var didSomethings = false
            if (voteSpot.voteSpotState == VoteSpotState.WAITING_FOR_PAYMENT) {
                didSomethings = payForVotes(user, voteSpot, didSomethings)
            }
            if (voteSpot.voteSpotState == VoteSpotState.OPEN) {
                castVote(data, voteSpot, user)
            }
            return didSomethings
        }
        return false
    }

    private fun castVote(
        data: GlobalGameData,
        voteSpot: RedisVoteSpot,
        user: RedisGameUser
    ) {
        val userVoteSpots: List<RedisUserVoteSpot>? = data.userVoteSpotsBySpotId[voteSpot.inGameId()]
        if (userVoteSpots != null && userVoteSpots.isNotEmpty()) {
            val userVoteSpot = userVoteSpots.firstOrNull { it.userId == user.inGameId() }
            if (userVoteSpot != null) {
                val users = data.users.values
                users.shuffled(random).forEach { targetUser ->
                    if (userVoteHandler.canVote(
                            userVoteSpot,
                            targetUser,
                            voteSpot,
                        )
                    ) {
                        userVoteHandler.castVote(
                            userVoteSpot,
                            targetUser,
                            users,
                            voteSpot,
                            userVoteSpots,
                            data
                        )
                    }
                }
            }
        }
    }

    private fun payForVotes(
        user: RedisGameUser,
        voteSpot: RedisVoteSpot,
        didSomethings: Boolean
    ): Boolean {
        var didSomethings1 = didSomethings
        if (inventoryHandler.userHaveItems(
                user,
                voteSpot.costItem,
                voteSpot.costValue ?: 0
            )
        ) {
            inventoryHandler.consumeItems(
                user,
                voteSpot.costItem,
                voteSpot.costValue
            )
            voteSpot.voteSpotState = VoteSpotState.OPEN
            voteSpotRedisVoteSpot.save(voteSpot)
            didSomethings1 = true
        }
        return didSomethings1
    }

    private fun voteSpots(
        data: GlobalGameData,
        user: RedisGameUser
    ): List<RedisVoteSpot> = data.voteSpots.filter {
        userLocationHandler.userCanSeeTarget(
            user,
            it,
            data.levelGeometryData,
            true
        )
    }.toList()
}