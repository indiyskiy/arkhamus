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
        castedAbilities: List<RedisAbilityCast>,
        currentGameTime: Long
    ) {
        castedAbilities.forEach { castedAbility ->
            when (castedAbility.state) {
                ACTIVE -> {
                    processActive(castedAbility, globalGameData)
                    processActiveEvent(castedAbility, globalGameData)
                    redisAbilityCastRepository.save(castedAbility)
                }

                ON_COOLDOWN -> {
                    processCooldown(castedAbility)
                    redisAbilityCastRepository.save(castedAbility)
                }

                else -> {}
            }
        }
    }

    private fun processActive(
        castedAbility: RedisAbilityCast,
        globalGameData: GlobalGameData
    ) {
        if (castedAbility.timeLeftActive > 0) {
            val timeAdd = min(castedAbility.timeLeftCooldown, ArkhamusOneTickLogic.TICK_DELTA)
            processNotPastEvent(castedAbility, timeAdd)
            if (castedAbility.timeLeftActive <= 0) {
                castedAbility.state = ON_COOLDOWN
                if (castedAbility.timeLeftCooldown <= 0) {
                    castedAbility.state = PAST
                }
                endActiveEvent(castedAbility, globalGameData)
            }
        } else {
            castedAbility.state = ON_COOLDOWN
        }
    }


    private fun processCooldown(castedAbility: RedisAbilityCast) {
        if (castedAbility.timeLeftCooldown > 0 || castedAbility.timeLeftActive > 0) {
            val timeAdd = min(castedAbility.timeLeftCooldown, ArkhamusOneTickLogic.TICK_DELTA)
            processNotPastEvent(castedAbility, timeAdd)
            if (castedAbility.timeLeftCooldown <= 0 && castedAbility.timeLeftActive <= 0) {
                castedAbility.state = PAST
            }
        } else {
            castedAbility.state = PAST
        }
    }

    private fun processActiveEvent(
        castedAbility: RedisAbilityCast,
        globalGameData: GlobalGameData
    ) {
        activeAbilityProcessors.filter {
            it.accepts(castedAbility)
        }.forEach { processor ->
            processor.processActive(castedAbility, globalGameData)
        }
    }

    private fun endActiveEvent(
        castedAbility: RedisAbilityCast,
        globalGameData: GlobalGameData
    ) {
        activeAbilityProcessors.filter {
            it.accepts(castedAbility)
        }.forEach { processor ->
            processor.finishActive(castedAbility, globalGameData)
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