package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.DEFAULT_ABILITY_COOLDOWN_MULTIPLIER
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors.ActiveAbilityProcessor
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameAbilityCastRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.InGameTimeEventState.*
import com.arkhamusserver.arkhamus.model.ingame.InGameAbilityCast
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.math.roundToLong

@Component
class OnTickAbilityCast(
    private val inGameAbilityCastRepository: InGameAbilityCastRepository,
    private val activeAbilityProcessors: List<ActiveAbilityProcessor>
) {

    @Transactional
    fun applyAbilityCasts(
        globalGameData: GlobalGameData,
        castAbilities: List<InGameAbilityCast>,
        timePassedMillis: Long
    ) {
        castAbilities.forEach { castAbility ->
            val user = globalGameData.users[castAbility.sourceUserId]
            val currentCooldownSpeed = user?.currentCooldownSpeed ?: DEFAULT_ABILITY_COOLDOWN_MULTIPLIER
            when (castAbility.state) {
                ACTIVE -> {
                    handleActiveEvent(castAbility, globalGameData)
                    pushActive(castAbility, globalGameData, timePassedMillis, currentCooldownSpeed)
                    inGameAbilityCastRepository.save(castAbility)
                }

                ON_COOLDOWN -> {
                    pushCooldown(castAbility, timePassedMillis, currentCooldownSpeed)
                    inGameAbilityCastRepository.save(castAbility)
                }

                else -> {}
            }
        }
    }

    private fun pushActive(
        castAbility: InGameAbilityCast,
        globalGameData: GlobalGameData,
        timePassedMillis: Long,
        currentCooldownSpeed: Double
    ) {
        if (castAbility.timeLeftActive > 0) {
            pushNotPastEvent(castAbility, timePassedMillis, currentCooldownSpeed)
            if (castAbility.timeLeftActive <= 0) {
                transitActiveToCooldown(castAbility, globalGameData)
            }
        } else {
            transitActiveToCooldown(castAbility, globalGameData)
            pushCooldown(castAbility, timePassedMillis, currentCooldownSpeed)
        }
    }

    private fun transitActiveToCooldown(
        castAbility: InGameAbilityCast,
        globalGameData: GlobalGameData
    ) {
        endActiveEvent(castAbility, globalGameData)
        castAbility.state = ON_COOLDOWN
        if (castAbility.timeLeftCooldown <= 0) {
            castAbility.state = PAST
        }
    }


    private fun pushCooldown(
        castAbility: InGameAbilityCast,
        timePassedMillis: Long,
        currentCooldownSpeed: Double
    ) {
        if (castAbility.timeLeftCooldown > 0) {
            pushNotPastEvent(castAbility, timePassedMillis, currentCooldownSpeed)
            if (castAbility.timeLeftCooldown <= 0) {
                castAbility.state = PAST
            }
        } else {
            castAbility.state = PAST
        }
    }

    private fun handleActiveEvent(
        castAbility: InGameAbilityCast,
        globalGameData: GlobalGameData
    ) {
        activeAbilityProcessors.filter {
            it.accepts(castAbility)
        }.forEach { processor ->
            processor.processActive(castAbility, globalGameData)
        }
    }

    private fun endActiveEvent(
        castAbility: InGameAbilityCast,
        globalGameData: GlobalGameData
    ) {
        activeAbilityProcessors.filter {
            it.accepts(castAbility)
        }.forEach { processor ->
            processor.finishActive(castAbility, globalGameData)
        }
    }

    private fun pushNotPastEvent(
        abilityCast: InGameAbilityCast,
        timePassedMillis: Long,
        currentCooldownSpeed: Double
    ) {
        abilityCast.timePast += (timePassedMillis * currentCooldownSpeed).roundToLong()
        abilityCast.timeLeftCooldown -= (timePassedMillis * currentCooldownSpeed).roundToLong()
        abilityCast.timeLeftActive -= timePassedMillis
    }
}
