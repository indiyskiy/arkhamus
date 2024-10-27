package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.banvote

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserVoteHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.banvote.VoteSpotCastRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.NettyRequestProcessor
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class VoteSpotCastRequestProcessor(
    private val voteHandler: UserVoteHandler
) : NettyRequestProcessor {

    companion object {
        private val logger = LoggerFactory.getLogger(VoteSpotCastRequestProcessor::class.java)
    }

    override fun accept(request: NettyTickRequestMessageDataHolder): Boolean {
        return request.requestProcessData is VoteSpotCastRequestProcessData
    }

    @Transactional
    override fun process(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        val gameData = requestDataHolder.requestProcessData as VoteSpotCastRequestProcessData
        val voteSpot = gameData.voteSpot
        val currentUserVoteSpot = gameData.currentUserVoteSpot
        val allUserVoteSpots = gameData.thisSpotUserInfos
        val targetUser = gameData.targetUser
        if (gameData.canVoteForTargetUser &&
            currentUserVoteSpot != null &&
            targetUser != null &&
            voteSpot != null
        ) {
            logger.info("casting vote from ${currentUserVoteSpot.userId} to ${targetUser.userId}")
            voteHandler.castVote(currentUserVoteSpot, targetUser, globalGameData, requestDataHolder, voteSpot)
            val bannedUser = voteHandler.applyBanMaybe(
                globalGameData.users.values,
                voteSpot,
                allUserVoteSpots,
                globalGameData
            )
            gameData.targetUserBanned = (bannedUser != null) && (bannedUser.userId == targetUser.userId)
            gameData.successfullyVoted = true
        }
    }

}
