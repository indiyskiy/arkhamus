package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.ritual

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.RitualHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.ritual.GodVoteCastRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.NettyRequestProcessor
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisAltarPollingRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.God
import com.arkhamusserver.arkhamus.model.redis.RedisAltarPolling
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class GodVoteCastRequestProcessor(
    private val redisAltarPollingRepository: RedisAltarPollingRepository,
    private val ritualHandler: RitualHandler
) : NettyRequestProcessor {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(GodVoteCastRequestProcessor::class.java)
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
                castGodVote(
                    god = god,
                    altarPolling = altarPolling,
                    gameData = godVoteCastRequestProcessData
                )
                ritualHandler.tryToForceStartRitual(allUsers, altarPolling, altars, altarHolder, events, game)
                godVoteCastRequestProcessData.executedSuccessfully = true
            }
        }
    }

    private fun castGodVote(
        god: God,
        altarPolling: RedisAltarPolling,
        gameData: GodVoteCastRequestProcessData
    ) {
        val userId: Long = gameData.gameUser!!.userId
        val godId = god.getId()
        altarPolling.userVotes[userId] = godId
        redisAltarPollingRepository.save(altarPolling)
    }
}