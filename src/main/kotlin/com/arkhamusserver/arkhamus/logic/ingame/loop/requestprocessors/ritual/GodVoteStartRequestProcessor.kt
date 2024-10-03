package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.ritual

import com.arkhamusserver.arkhamus.logic.ingame.logic.Location
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.RitualHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.TimeEventHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.ritual.GodVoteStartRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.NettyRequestProcessor
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisAltarHolderRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisAltarPollingRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.core.God
import com.arkhamusserver.arkhamus.model.enums.ingame.MapAltarPollingState
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapAltarState
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.redis.RedisAltar
import com.arkhamusserver.arkhamus.model.redis.RedisAltarPolling
import com.fasterxml.uuid.Generators
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class GodVoteStartRequestProcessor(
    private val redisAltarPollingRepository: RedisAltarPollingRepository,
    private val redisAltarHolderRepository: RedisAltarHolderRepository,
    private val ritualHandler: RitualHandler,
    private val timeEventHandler: TimeEventHandler,
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
                createGodVoteStartProcess(
                    gameId = requestDataHolder.gameSession!!.id!!,
                    globalTimer = globalGameData.game.globalTimer,
                    sourceUserId = requestDataHolder.userAccount.id!!,
                    altar = altar
                )
                val altarPolling = createGodVote(
                    god = god,
                    altar = altar,
                    globalGameData = globalGameData,
                    gameData = godVoteStartRequestProcessData
                )
                globalGameData.altarHolder?.state = MapAltarState.VOTING
                globalGameData.altarHolder?.let { redisAltarHolderRepository.save(it) }

                ritualHandler.tryToForceStartRitual(allUsers, altarPolling, altars, altarHolder, events, game)

                godVoteStartRequestProcessData.executedSuccessfully = true
            }
        }
    }

    private fun createGodVote(
        god: God,
        altar: RedisAltar,
        globalGameData: GlobalGameData,
        gameData: GodVoteStartRequestProcessData
    ): RedisAltarPolling {
        val userId: Long = gameData.gameUser!!.userId
        val godId = god.getId()
        val altarPolling = RedisAltarPolling(
            id = Generators.timeBasedEpochGenerator().generate().toString(),
            started = globalGameData.game.globalTimer,
            altarId = altar.inGameId(),
            gameId = globalGameData.game.gameId!!,
            startedUserId = userId,
            userVotes = mutableMapOf(userId to godId),
            state = MapAltarPollingState.ONGOING
        )
        redisAltarPollingRepository.save(altarPolling)
        return altarPolling
    }

    private fun createGodVoteStartProcess(
        gameId: Long,
        globalTimer: Long,
        sourceUserId: Long,
        altar: RedisAltar
    ) {
        timeEventHandler.createEvent(
            gameId = gameId,
            eventType = RedisTimeEventType.ALTAR_VOTING,
            startDateTime = globalTimer,
            sourceObjectId = sourceUserId,
            location = Location(altar.x, altar.y, altar.z),
        )
    }

}