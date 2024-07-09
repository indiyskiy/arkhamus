package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts

import com.arkhamusserver.arkhamus.logic.ingame.loop.ArkhamusOneTickLogic
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors.ActiveAbilityProcessor
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisAbilityCastRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventState.*
import com.arkhamusserver.arkhamus.model.redis.RedisAbilityCast
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.math.min

@Component
class OnTickAbilityCast(
    private val redisAbilityCastRepository: RedisAbilityCastRepository,
    private val activeAbilityProcessors: List<ActiveAbilityProcessor>
) {
    companion object {
        var logger: Logger = LoggerFactory.getLogger(OnTickAbilityCast::class.java)
    }

    fun applyAbilityCasts(
        globalGameData: GlobalGameData,
        castAbilities: List<RedisAbilityCast>,
        currentGameTime: Long
    ) {
        castAbilities.forEach { castAbility ->
            when (castAbility.state) {
                ACTIVE -> {
                    processActive(castAbility, globalGameData)
                    processActiveEvent(castAbility, globalGameData)
                    redisAbilityCastRepository.save(castAbility)
                }

                ON_COOLDOWN -> {
                    processCooldown(castAbility)
                    redisAbilityCastRepository.save(castAbility)
                }

                else -> {}
            }
        }
    }

    private fun processActive(
        castAbility: RedisAbilityCast,
        globalGameData: GlobalGameData
    ) {
        if (castAbility.timeLeftActive > 0) {
            val timeAdd = min(castAbility.timeLeftCooldown, ArkhamusOneTickLogic.TICK_DELTA)
            processNotPastEvent(castAbility, timeAdd)
            if (castAbility.timeLeftActive <= 0) {
                castAbility.state = ON_COOLDOWN
                if (castAbility.timeLeftCooldown <= 0) {
                    castAbility.state = PAST
                }
                endActiveEvent(castAbility, globalGameData)
            }
        } else {
            castAbility.state = ON_COOLDOWN
        }
    }


    private fun processCooldown(castAbility: RedisAbilityCast) {
        if (castAbility.timeLeftCooldown > 0 || castAbility.timeLeftActive > 0) {
            val timeAdd = min(castAbility.timeLeftCooldown, ArkhamusOneTickLogic.TICK_DELTA)
            processNotPastEvent(castAbility, timeAdd)
            if (castAbility.timeLeftCooldown <= 0 && castAbility.timeLeftActive <= 0) {
                castAbility.state = PAST
            }
        } else {
            castAbility.state = PAST
        }
    }

    private fun processActiveEvent(
        castAbility: RedisAbilityCast,
        globalGameData: GlobalGameData
    ) {
        activeAbilityProcessors.filter {
            it.accepts(castAbility)
        }.forEach { processor ->
            processor.processActive(castAbility, globalGameData)
        }
    }

    private fun endActiveEvent(
        castAbility: RedisAbilityCast,
        globalGameData: GlobalGameData
    ) {
        activeAbilityProcessors.filter {
            it.accepts(castAbility)
        }.forEach { processor ->
            processor.finishActive(castAbility, globalGameData)
        }
    }

    private fun processNotPastEvent(
        abilityCast: RedisAbilityCast,
        timeAdd: Long,
    ) {
        if (abilityCast.timeLeftCooldown > 0 || abilityCast.timeLeftActive > 0) {
            abilityCast.timePast += timeAdd
            abilityCast.timeLeftCooldown -= timeAdd
            abilityCast.timeLeftActive -= timeAdd
        }
    }
}