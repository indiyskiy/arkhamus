package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors.ActiveAbilityProcessor
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameAbilityActiveCastRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.InGameTimeEventState.ACTIVE
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.InGameTimeEventState.PAST
import com.arkhamusserver.arkhamus.model.ingame.InGameAbilityActiveCast
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OnTickAbilityActiveCast(
    private val inGameAbilityActiveCastRepository: InGameAbilityActiveCastRepository,
    private val activeAbilityProcessors: List<ActiveAbilityProcessor>,
) {

    @Transactional
    fun applyAbilityCasts(
        globalGameData: GlobalGameData,
        castAbilities: List<InGameAbilityActiveCast>,
        timePassedMillis: Long
    ) {
        castAbilities.forEach { castAbility ->
            when (castAbility.state) {
                ACTIVE -> {
                    handleActiveEvent(castAbility, globalGameData)
                    pushActive(castAbility, globalGameData, timePassedMillis)
                    inGameAbilityActiveCastRepository.save(castAbility)
                }

                PAST -> {
                    inGameAbilityActiveCastRepository.delete(castAbility)
                }

                else -> {}
            }
        }
    }

    private fun pushActive(
        castAbility: InGameAbilityActiveCast,
        globalGameData: GlobalGameData,
        timePassedMillis: Long,
    ) {
        if (castAbility.timeLeftActive > 0) {
            pushNotPastEvent(castAbility, timePassedMillis)
            if (castAbility.timeLeftActive <= 0) {
                transitActiveToPast(castAbility, globalGameData)
            }
        } else {
            transitActiveToPast(castAbility, globalGameData)
        }
    }

    private fun transitActiveToPast(
        castAbility: InGameAbilityActiveCast,
        globalGameData: GlobalGameData
    ) {
        endActiveEvent(castAbility, globalGameData)
        castAbility.state = PAST
    }

    private fun handleActiveEvent(
        castAbility: InGameAbilityActiveCast,
        globalGameData: GlobalGameData
    ) {
        activeAbilityProcessors.filter {
            it.accepts(castAbility)
        }.forEach { processor ->
            processor.processActive(castAbility, globalGameData)
        }
    }

    private fun endActiveEvent(
        castAbility: InGameAbilityActiveCast,
        globalGameData: GlobalGameData
    ) {
        activeAbilityProcessors.filter {
            it.accepts(castAbility)
        }.forEach { processor ->
            processor.finishActive(castAbility, globalGameData)
        }
    }

    private fun pushNotPastEvent(
        abilityCast: InGameAbilityActiveCast,
        timePassedMillis: Long,
    ) {
        abilityCast.timePast += timePassedMillis
        abilityCast.timeLeftActive -= timePassedMillis
    }
}
