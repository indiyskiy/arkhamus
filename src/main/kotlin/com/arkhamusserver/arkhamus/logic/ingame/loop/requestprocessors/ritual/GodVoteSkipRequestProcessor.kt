package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.ritual

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.RitualHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.ritual.GodVoteSkipRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.NettyRequestProcessor
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisAltarPollingRepository
import com.arkhamusserver.arkhamus.model.redis.RedisAltarPolling
import org.springframework.stereotype.Component

@Component
class GodVoteSkipRequestProcessor(
    private val redisAltarPollingRepository: RedisAltarPollingRepository,
    private val ritualHandler: RitualHandler
) : NettyRequestProcessor {

    override fun accept(request: NettyTickRequestMessageDataHolder): Boolean {
        return request.requestProcessData is GodVoteSkipRequestProcessData
    }

    override fun process(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        val godVoteSkipRequestProcessData = requestDataHolder.requestProcessData as GodVoteSkipRequestProcessData
        val altarPolling = globalGameData.altarPolling
        val events = globalGameData.timeEvents
        val allUsers = globalGameData.users.values
        val altars = globalGameData.altars
        val altarHolder = globalGameData.altarHolder
        val game = globalGameData.game
        if (altarPolling != null) {
            val canSkip = godVoteSkipRequestProcessData.canSkip
            if (canSkip) {
                skipVote(
                    altarPolling = altarPolling,
                    gameData = godVoteSkipRequestProcessData
                )
                ritualHandler.tryToForceStartRitual(allUsers, altarPolling, altars, altarHolder, events, game)
                godVoteSkipRequestProcessData.executedSuccessfully = true
            }
        }
    }

    private fun skipVote(
        altarPolling: RedisAltarPolling,
        gameData: GodVoteSkipRequestProcessData
    ) {
        val userId: Long = gameData.gameUser!!.userId
        altarPolling.skippedUsers += userId
        redisAltarPollingRepository.save(altarPolling)
    }
}