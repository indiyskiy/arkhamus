package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts

import com.arkhamusserver.arkhamus.logic.ingame.loop.ArkhamusOneTickLogic
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisAbilityCastRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.redis.RedisAbilityCast
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
        castedAbilities.forEach { castedAbility ->
            if (castedAbility.state == RedisTimeEventState.ACTIVE) {
                if (castedAbility.timeLeft > 0) {
                    val timeAdd = min(castedAbility.timeLeft, ArkhamusOneTickLogic.TICK_DELTA)
                    processActiveEvent(castedAbility, timeAdd)
                } else {
                    castedAbility.state = RedisTimeEventState.PAST
                    redisAbilityCastRepository.save(castedAbility)
                }
            }
        }
    }

    private fun processActiveEvent(
        abilityCast: RedisAbilityCast,
        timeAdd: Long,
    ) {
        if (abilityCast.timeLeft > 0) {
            abilityCast.timePast += timeAdd
            abilityCast.timeLeft -= timeAdd
        }
        if (abilityCast.timeLeft <= 0) {
            abilityCast.state = RedisTimeEventState.PAST
        }
        redisAbilityCastRepository.save(abilityCast)
    }
}