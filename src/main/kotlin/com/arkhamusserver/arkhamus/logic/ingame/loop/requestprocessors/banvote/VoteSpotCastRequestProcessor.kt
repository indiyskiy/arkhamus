package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.banvote

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserVoteHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.banvote.VoteSpotCastRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.NettyRequestProcessor
import org.springframework.stereotype.Component

@Component
class VoteSpotCastRequestProcessor(
    private val voteHandler: UserVoteHandler,
) : NettyRequestProcessor {

    override fun accept(request: NettyTickRequestMessageDataHolder): Boolean {
        return request.requestProcessData is VoteSpotCastRequestProcessData
    }

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
        if (gameData.canVoteForTargetUser && currentUserVoteSpot != null && targetUser != null && voteSpot != null) {
            val bannedUser = voteHandler.castVote(
                currentUser = gameData.gameUser!!,
                currentUserVoteSpot = currentUserVoteSpot,
                targetUser = targetUser,
                users = globalGameData.users.values,
                voteSpot = voteSpot,
                allUserVoteSpots = allUserVoteSpots,
                globalGameData = globalGameData
            )
            gameData.targetUserBanned = (bannedUser != null) && (bannedUser.inGameId() == targetUser.userId)
            gameData.successfullyVoted = true
        }
    }

}
