package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.banvote

import com.arkhamusserver.arkhamus.logic.ingame.logic.Location
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.TimeEventHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.banvote.CallForBanVoteRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.NettyRequestProcessor
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class CallForBanVoteRequestProcessor(
    private val eventHandler: TimeEventHandler
) : NettyRequestProcessor {

    override fun accept(request: NettyTickRequestMessageDataHolder): Boolean {
        return request.requestProcessData is CallForBanVoteRequestProcessData
    }

    @Transactional
    override fun process(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        val gameData = requestDataHolder.requestProcessData as CallForBanVoteRequestProcessData
        if (gameData.canCallForVote) {
            createEvent(gameData, globalGameData)
            gameData.gameUser?.let { it.callToArms -= 1 }
            gameData.successfullyCalled = true
        }
    }

    private fun createEvent(
        gameData: CallForBanVoteRequestProcessData,
        globalGameData: GlobalGameData
    ) {
        val voteSpot = gameData.voteSpot
        val threshold = gameData.threshold
        eventHandler.createEvent(
            game = globalGameData.game,
            eventType = RedisTimeEventType.CALL_FOR_BAN_VOTE,
            sourceObject = gameData.gameUser,
            targetObject = voteSpot,
            location = threshold?.let { Location(it.x, it.y, it.z) },
            timeLeft = RedisTimeEventType.CALL_FOR_BAN_VOTE.getDefaultTime()
        )
    }
}