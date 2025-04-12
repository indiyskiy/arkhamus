package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ritual

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserMadnessHandler
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapAltarPollingState
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapAltarState
import com.arkhamusserver.arkhamus.model.ingame.InGameAltar
import com.arkhamusserver.arkhamus.model.ingame.InGameAltarHolder
import com.arkhamusserver.arkhamus.model.ingame.InGameAltarPolling
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class GodVoteHandler(
    private val madnessHandler: UserMadnessHandler,
) {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(InventoryHandler::class.java)
    }

    fun canBeStarted(
        altarHolder: InGameAltarHolder?,
        altar: InGameAltar?,
    ): Boolean =
        getAltarIsOpen(altarHolder) && getAltarExist(altar)

    fun canVote(
        altarPolling: InGameAltarPolling?,
        altarHolder: InGameAltarHolder?,
        user: InGameUser
    ) = altarPolling != null &&
            altarHolder != null &&
            isVoteProcessOpen(altarPolling, altarHolder) &&
            ((altarPolling.userVotes[user.inGameId()]) == null) &&
            usersCanPossiblyVote(user) &&
            !skipped(altarPolling, user.inGameId())

    fun isVoteProcessOpen(
        altarPolling: InGameAltarPolling?,
        altarHolder: InGameAltarHolder?
    ) =
        (altarPolling?.state == MapAltarPollingState.ONGOING) &&
                (altarHolder?.state == MapAltarState.VOTING)

    fun everybodyVoted(
        allUsers: Collection<InGameUser>,
        altarPolling: InGameAltarPolling
    ): Boolean {
        logger.info("everybodyVoted?")
        val canVote = usersCanPossiblyVote(allUsers)
        val canVoteIdsSet = canVote.map { it.inGameId() }.toSet()
        val votesStillRelevant = altarPolling.userVotes.filter { it.key in canVoteIdsSet }
        val votedUserIdsSet = votesStillRelevant.map { it.key }.toSet()
        logger.info("voted - ${votedUserIdsSet.size}")
        val skipped = altarPolling.skippedUsers.filter { it in canVoteIdsSet }.toSet()
        logger.info("skipped ${skipped.size}")
        val notVoted = canVoteIdsSet.filter {
            it !in votedUserIdsSet &&
                    it !in skipped
        }
        logger.info("not voted ${notVoted.size}")
        val result = notVoted.isEmpty()
        logger.info("result $result")
        return result
    }

    fun usersCanPossiblyVote(allUsers: Collection<InGameUser>) =
        madnessHandler.filterNotMad(allUsers)

    fun usersCanPossiblyVote(user: InGameUser) =
        !madnessHandler.isCompletelyMad(user)

    private fun skipped(altarPolling: InGameAltarPolling, userId: Long): Boolean =
        altarPolling.skippedUsers.contains(userId)

    private fun getAltarExist(altar: InGameAltar?) = altar != null

    private fun getAltarIsOpen(altarHolder: InGameAltarHolder?) =
        altarHolder?.state == MapAltarState.OPEN
}