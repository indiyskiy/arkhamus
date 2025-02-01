package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.madness

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserVoteHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameVoteSpotRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.VoteSpotState
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.InGameUserVoteSpot
import com.arkhamusserver.arkhamus.model.ingame.InGameVoteSpot
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class VoteMadnessHandler(
    private val userLocationHandler: UserLocationHandler,
    private val inventoryHandler: InventoryHandler,
    private val inGameVoteSpot: InGameVoteSpotRepository,
    private val userVoteHandler: UserVoteHandler,
) {

    companion object {
        private val random = Random(System.currentTimeMillis())
    }

    fun voteForSomeone(
        user: InGameUser,
        data: GlobalGameData,
    ): Boolean {
        val voteSpots = voteSpots(data, user)
        if (voteSpots.isNotEmpty()) {
            val voteSpot = voteSpots.random(random)
            var didSomethings = false
            if (voteSpot.voteSpotState == VoteSpotState.WAITING_FOR_PAYMENT) {
                didSomethings = payForVotes(user, voteSpot)
            }
            if (voteSpot.voteSpotState == VoteSpotState.OPEN) {
                didSomethings = didSomethings || castVote(data, voteSpot, user)
            }
            return didSomethings
        }
        return false
    }

    private fun castVote(
        data: GlobalGameData,
        voteSpot: InGameVoteSpot,
        user: InGameUser
    ): Boolean {
        val userVoteSpots: List<InGameUserVoteSpot>? = data.userVoteSpotsBySpotId[voteSpot.inGameId()]
        if (userVoteSpots != null && userVoteSpots.isNotEmpty()) {
            val userVoteSpot = userVoteSpots.firstOrNull { it.userId == user.inGameId() }
            val currentUser = data.users.values.firstOrNull { it.inGameId() == user.inGameId() }
            if (userVoteSpot != null && currentUser != null) {
                val users = data.users.values
                var anyVotes = false
                users.shuffled(random).forEach { targetUser ->
                    if (userVoteHandler.canVote(
                            userVoteSpot,
                            targetUser,
                            voteSpot,
                        )
                    ) {
                        userVoteHandler.castVote(
                            currentUser,
                            userVoteSpot,
                            targetUser,
                            users,
                            voteSpot,
                            userVoteSpots,
                            data
                        )
                        anyVotes = true
                        if (voteSpot.voteSpotState != VoteSpotState.OPEN) {
                            return true
                        }
                    }
                }
                return anyVotes
            }
        }
        return false
    }

    private fun payForVotes(
        user: InGameUser,
        voteSpot: InGameVoteSpot
    ): Boolean {
        if (inventoryHandler.userHaveItems(
                user,
                voteSpot.costItem,
                voteSpot.costValue
            )
        ) {
            inventoryHandler.consumeItems(
                user,
                voteSpot.costItem,
                voteSpot.costValue
            )
            voteSpot.voteSpotState = VoteSpotState.OPEN
            inGameVoteSpot.save(voteSpot)
            return true
        }
        return false
    }

    private fun voteSpots(
        data: GlobalGameData,
        user: InGameUser
    ): List<InGameVoteSpot> = data.voteSpots.filter {
        userLocationHandler.userCanSeeTarget(
            user,
            it,
            data.levelGeometryData,
            true
        ) && userLocationHandler.userInInteractionRadius(user, it)
    }.toList()
}