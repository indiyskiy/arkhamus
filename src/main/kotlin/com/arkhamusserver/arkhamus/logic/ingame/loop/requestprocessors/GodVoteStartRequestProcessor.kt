package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.GodVoteStartRequestProcessData
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisAltarHolderRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisAltarPollingRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisTimeEventRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.*
import com.arkhamusserver.arkhamus.model.redis.RedisAltar
import com.arkhamusserver.arkhamus.model.redis.RedisAltarPolling
import com.arkhamusserver.arkhamus.model.redis.RedisTimeEvent
import com.fasterxml.uuid.Generators
import org.springframework.stereotype.Component

@Component
class GodVoteStartRequestProcessor(
    private val timeEventRepository: RedisTimeEventRepository,
    private val redisAltarPollingRepository: RedisAltarPollingRepository,
    private val redisAltarHolderRepository: RedisAltarHolderRepository
) : NettyRequestProcessor {

    override fun accept(request: NettyTickRequestMessageDataHolder): Boolean {
        return request.requestProcessData is GodVoteStartRequestProcessData
    }

    override fun process(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        val godVoteStartRequestProcessData = requestDataHolder.requestProcessData as GodVoteStartRequestProcessData
        val god = godVoteStartRequestProcessData.starterGod
        val altar = godVoteStartRequestProcessData.altar
        if (god != null && altar != null) {
            val canBeStarted = godVoteStartRequestProcessData.canBeStarted
            if (canBeStarted) {
                createGodVoteStartProcess(
                    gameId = requestDataHolder.gameSession!!.id!!,
                    globalTimer = globalGameData.game.globalTimer,
                    sourceUserId = requestDataHolder.userAccount.id!!,
                    altar = altar
                )
                createGodVote(
                    god = god,
                    altar = altar,
                    globalGameData = globalGameData,
                    gameData = godVoteStartRequestProcessData
                )
                globalGameData.altarHolder.state = MapAltarState.VOTING
                redisAltarHolderRepository.save(globalGameData.altarHolder)

                godVoteStartRequestProcessData.executedSuccessfully = true
            }
        }
    }

    private fun createGodVote(
        god: God,
        altar: RedisAltar,
        globalGameData: GlobalGameData,
        gameData: GodVoteStartRequestProcessData
    ) {
        val userId: Long = gameData.gameUser!!.userId
        val godId = god.getId()
        val altarPolling = RedisAltarPolling(
            id = Generators.timeBasedEpochGenerator().generate().toString(),
            started = globalGameData.game.globalTimer,
            altarId = altar.altarId,
            gameId = globalGameData.game.gameId!!,
            startedUserId = userId,
            userVotes = mutableMapOf(userId to godId),
            state = MapAltarPollingState.ONGOING
        )
        redisAltarPollingRepository.save(altarPolling)
    }

    private fun createGodVoteStartProcess(
        gameId: Long,
        globalTimer: Long,
        sourceUserId: Long,
        altar: RedisAltar
    ) {
        val godVoteStartProcess = RedisTimeEvent(
            id = Generators.timeBasedEpochGenerator().generate().toString(),
            gameId = gameId,
            sourceUserId = sourceUserId,
            targetUserId = null,
            timeStart = globalTimer,
            timeLeft = RedisTimeEventType.ALTAR_VOTING.getDefaultTime(),
            timePast = 0L,
            type = RedisTimeEventType.ALTAR_VOTING,
            state = RedisTimeEventState.ACTIVE,
            xLocation = altar.x,
            yLocation = altar.y
        )
        timeEventRepository.save(godVoteStartProcess)
    }

}