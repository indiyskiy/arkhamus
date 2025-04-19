package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.ritual

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ritual.RitualHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.ritual.GodVoteCastRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.NettyRequestProcessor
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class GodVoteCastRequestProcessor(
    private val ritualHandler: RitualHandler
) : NettyRequestProcessor {

    companion object {
        private val logger = LoggingUtils.getLogger<GodVoteCastRequestProcessor>()
    }

    override fun accept(request: NettyTickRequestMessageDataHolder): Boolean {
        return request.requestProcessData is GodVoteCastRequestProcessData
    }

    @Transactional
    override fun process(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        logger.info("GodVoteCast process")
        val godVoteCastRequestProcessData = requestDataHolder.requestProcessData as GodVoteCastRequestProcessData
        val god = godVoteCastRequestProcessData.votedGod
        val altarPolling = globalGameData.altarPolling
        val altarHolder = globalGameData.altarHolder
        val altars = globalGameData.altars
        val game = globalGameData.game
        val allUsers = globalGameData.users.values
        val events = globalGameData.timeEvents
        if (god != null && altarPolling != null) {
            val canVote = godVoteCastRequestProcessData.canVote
            if (canVote) {
                ritualHandler.castGodVote(
                    god = god,
                    altar = godVoteCastRequestProcessData.altar!!,
                    currentGameUser = godVoteCastRequestProcessData.gameUser!!,
                    altarPolling = altarPolling,
                    gameData = godVoteCastRequestProcessData,
                    altarHolder = altarHolder,
                    altars = altars,
                    game = game,
                    allUsers = allUsers,
                    events = events
                )
                godVoteCastRequestProcessData.executedSuccessfully = true
            }
        }
    }
}