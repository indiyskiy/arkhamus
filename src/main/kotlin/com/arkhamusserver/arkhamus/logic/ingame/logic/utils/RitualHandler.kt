package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisAltarHolderRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisAltarPollingRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisTimeEventRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.MapAltarPollingState
import com.arkhamusserver.arkhamus.model.enums.ingame.MapAltarState
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.redis.RedisAltarHolder
import com.arkhamusserver.arkhamus.model.redis.RedisAltarPolling
import com.arkhamusserver.arkhamus.model.redis.RedisTimeEvent
import com.fasterxml.uuid.Generators
import org.springframework.stereotype.Component

@Component
class RitualHandler(
    private val timeEventRepository: RedisTimeEventRepository,
    private val redisAltarPollingRepository: RedisAltarPollingRepository,
    private val redisAltarHolderRepository: RedisAltarHolderRepository,
) {
     fun failRitual(
        globalGameData: GlobalGameData,
        it: RedisAltarPolling
    ): RedisTimeEvent {
        globalGameData.altarPolling?.state = MapAltarPollingState.FAILED
        redisAltarPollingRepository.save(it)

        globalGameData.altarHolder.state = MapAltarState.ON_HOLD
        redisAltarHolderRepository.save(globalGameData.altarHolder)

        val cooldownEvent = RedisTimeEvent(
            id = Generators.timeBasedEpochGenerator().generate().toString(),
            gameId = globalGameData.game.gameId!!,
            sourceUserId = null,
            targetUserId = null,
            timeStart = globalGameData.game.globalTimer,
            timePast = 0L,
            timeLeft = RedisTimeEventType.ALTAR_VOTING_COOLDOWN.getDefaultTime(),
            type = RedisTimeEventType.ALTAR_VOTING_COOLDOWN,
            state = RedisTimeEventState.ACTIVE,
            xLocation = null,
            yLocation = null,
        )
        return timeEventRepository.save(cooldownEvent)
    }


    fun finishAltarPolling(
        it: RedisAltarPolling,
        globalGameData: GlobalGameData
    ): RedisAltarHolder {
        redisAltarPollingRepository.delete(it)

        globalGameData.altarHolder.state = MapAltarState.OPEN
        return redisAltarHolderRepository.save(globalGameData.altarHolder)
    }
}