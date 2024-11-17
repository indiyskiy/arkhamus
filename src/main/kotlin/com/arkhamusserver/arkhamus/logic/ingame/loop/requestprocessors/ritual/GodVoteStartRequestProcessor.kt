package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.ritual

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ritual.RitualHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.ritual.GodVoteStartRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.NettyRequestProcessor
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class GodVoteStartRequestProcessor(
    private val ritualHandler: RitualHandler,
) : NettyRequestProcessor {

    override fun accept(request: NettyTickRequestMessageDataHolder): Boolean {
        return request.requestProcessData is GodVoteStartRequestProcessData
    }

    @Transactional
    override fun process(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        val godVoteStartRequestProcessData = requestDataHolder.requestProcessData as GodVoteStartRequestProcessData
        val god = godVoteStartRequestProcessData.votedGod
        val altar = godVoteStartRequestProcessData.altar
        if (god != null && altar != null) {
            val canBeStarted = godVoteStartRequestProcessData.canBeStarted
            val events = globalGameData.timeEvents
            val allUsers = globalGameData.users.values
            val altars = globalGameData.altars
            val altarHolder = globalGameData.altarHolder
            val game = globalGameData.game
            if (canBeStarted) {
                ritualHandler.godVoteStart(
                    globalGameData,
                    altar,
                    god,
                    godVoteStartRequestProcessData.gameUser!!,
                    allUsers,
                    altars,
                    altarHolder,
                    events,
                    game
                )
                godVoteStartRequestProcessData.executedSuccessfully = true
            }
        }
    }

}