package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts

import com.arkhamusserver.arkhamus.logic.ingame.loop.ArkhamusOneTickLogic
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors.ActiveAbilityProcessor
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisAbilityCastRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.RedisTimeEventState.*
import com.arkhamusserver.arkhamus.model.redis.RedisAbilityCast
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OnTickAbilityCast(
    private val redisAbilityCastRepository: RedisAbilityCastRepository,
    private val activeAbilityProcessors: List<ActiveAbilityProcessor>
) {
    companion object {
        var logger: Logger = LoggerFactory.getLogger(OnTickAbilityCast::class.java)
    }

    @Transactional
    fun applyAbilityCasts(
        globalGameData: GlobalGameData,
        castAbilities: List<RedisAbilityCast>,
    ) {
        castAbilities.forEach { castAbility ->
            when (castAbility.state) {
                ACTIVE -> {
                    handleActiveEvent(castAbility, globalGameData)
                    pushActive(castAbility, globalGameData)
                    redisAbilityCastRepository.save(castAbility)
                }

                ON_COOLDOWN -> {
                    pushCooldown(castAbility)
                    redisAbilityCastRepository.save(castAbility)
                }

                else -> {}
            }
        }
    }

    private fun pushActive(
        castAbility: RedisAbilityCast,
        globalGameData: GlobalGameData
    ) {
        if (castAbility.timeLeftActive > 0) {
            pushNotPastEvent(castAbility)
            if (castAbility.timeLeftActive <= 0) {
                transitActiveToCooldown(castAbility, globalGameData)
            }
        } else {
            transitActiveToCooldown(castAbility, globalGameData)
            pushCooldown(castAbility)
        }
    }

    private fun transitActiveToCooldown(
        castAbility: RedisAbilityCast,
        globalGameData: GlobalGameData
    ) {
        endActiveEvent(castAbility, globalGameData)
        castAbility.state = ON_COOLDOWN
        if (castAbility.timeLeftCooldown <= 0) {
            castAbility.state = PAST
        }
    }


    private fun pushCooldown(castAbility: RedisAbilityCast) {
        if (castAbility.timeLeftCooldown > 0) {
            pushNotPastEvent(castAbility)
            if (castAbility.timeLeftCooldown <= 0) {
                castAbility.state = PAST
            }
        } else {
            castAbility.state = PAST
        }
    }


    private fun handleActiveEvent(
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

    private fun pushNotPastEvent(
        abilityCast: RedisAbilityCast
    ) {
        abilityCast.timePast += ArkhamusOneTickLogic.TICK_DELTA
        abilityCast.timeLeftCooldown -= ArkhamusOneTickLogic.TICK_DELTA
        abilityCast.timeLeftActive -= ArkhamusOneTickLogic.TICK_DELTA
    }
}