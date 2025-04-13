package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors.ActiveAbilityProcessor
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameAbilityCastRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.InGameTimeEventState.*
import com.arkhamusserver.arkhamus.model.ingame.InGameAbilityCast
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OnTickAbilityCast(
    private val inGameAbilityCastRepository: InGameAbilityCastRepository,
    private val activeAbilityProcessors: List<ActiveAbilityProcessor>
) {
    companion object {
        var logger: Logger = LoggerFactory.getLogger(OnTickAbilityCast::class.java)
    }

    @Transactional
    fun applyAbilityCasts(
        globalGameData: GlobalGameData,
        castAbilities: List<InGameAbilityCast>,
        timePassedMillis: Long
    ) {
        castAbilities.forEach { castAbility ->
            when (castAbility.state) {
                ACTIVE -> {
                    handleActiveEvent(castAbility, globalGameData)
                    pushActive(castAbility, globalGameData, timePassedMillis)
                    inGameAbilityCastRepository.save(castAbility)
                }

                ON_COOLDOWN -> {
                    pushCooldown(castAbility, timePassedMillis)
                    inGameAbilityCastRepository.save(castAbility)
                }

                else -> {}
            }
        }
    }

    private fun pushActive(
        castAbility: InGameAbilityCast,
        globalGameData: GlobalGameData,
        timePassedMillis: Long
    ) {
        if (castAbility.timeLeftActive > 0) {
            pushNotPastEvent(castAbility, timePassedMillis)
            if (castAbility.timeLeftActive <= 0) {
                transitActiveToCooldown(castAbility, globalGameData)
            }
        } else {
            transitActiveToCooldown(castAbility, globalGameData)
            pushCooldown(castAbility, timePassedMillis)
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


    private fun pushCooldown(castAbility: InGameAbilityCast, timePassedMillis: Long) {
        if (castAbility.timeLeftCooldown > 0) {
            pushNotPastEvent(castAbility, timePassedMillis)
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
        timePassedMillis: Long
    ) {
        abilityCast.timePast += timePassedMillis
        abilityCast.timeLeftCooldown -= timePassedMillis
        abilityCast.timeLeftActive -= timePassedMillis
    }
}