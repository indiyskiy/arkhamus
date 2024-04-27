package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts

import com.arkhamusserver.arkhamus.logic.ingame.loop.ArkhamusOneTickLogic
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisAbilityCastRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.redis.RedisAbilityCast
import com.arkhamusserver.arkhamus.model.redis.RedisTimeEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.math.min

@Component
class OnTickAbilityCast(
    private val redisAbilityCastRepository: RedisAbilityCastRepository,
) {
    companion object {
        var logger: Logger = LoggerFactory.getLogger(OnTickAbilityCast::class.java)
    }


    fun applyAbilityCasts(
        globalGameData: GlobalGameData,
        castedAbilities: List<RedisAbilityCast>,
        currentGameTime: Long
    ) {
        castedAbilities.mapNotNull { castedAbility ->
            if (castedAbility.state == RedisTimeEventState.ACTIVE && castedAbility.timeLeft > 0) {
                val timeAdd = min(castedAbility.timeLeft, ArkhamusOneTickLogic.TICK_DELTA)
                processActiveEvent(castedAbility, timeAdd)
            } else {
                null
            }
        }
    }

    private fun processActiveEvent(
        event: RedisAbilityCast,
        timeAdd: Long,
    ) {
        if (event.timeLeft > 0) {
            event.timePast += timeAdd
            event.timeLeft -= timeAdd
        }
        if (event.timeLeft <= 0) {
            event.state = RedisTimeEventState.PAST
        }
        redisAbilityCastRepository.save(event)
    }
}