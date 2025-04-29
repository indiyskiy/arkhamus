package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameAbilityCooldownRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.InGameTimeEventState.*
import com.arkhamusserver.arkhamus.model.ingame.InGameAbilityCooldown
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.math.roundToLong

@Component
class OnTickAbilityCooldown(
    private val inGameAbilityCooldownRepository: InGameAbilityCooldownRepository,
    private val globalGameSettings: GlobalGameSettings
) {

    @Transactional
    fun applyAbilityCooldown(
        globalGameData: GlobalGameData,
        cooldownAbilities: List<InGameAbilityCooldown>,
        timePassedMillis: Long
    ) {
        cooldownAbilities.forEach { castAbility ->
            val user = globalGameData.users[castAbility.sourceUserId]
            val currentCooldownSpeed = user?.currentCooldownSpeed ?: globalGameSettings.defaultAbilityCooldownMultiplier
            when (castAbility.state) {
               ON_COOLDOWN -> {
                    pushCooldown(castAbility, timePassedMillis, currentCooldownSpeed)
                    inGameAbilityCooldownRepository.save(castAbility)
                }
                PAST -> {
                    inGameAbilityCooldownRepository.delete(castAbility)
                }
                else -> {}
            }
        }
    }

    private fun pushCooldown(
        castAbility: InGameAbilityCooldown,
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

    private fun pushNotPastEvent(
        abilityCast: InGameAbilityCooldown,
        timePassedMillis: Long,
        currentCooldownSpeed: Double
    ) {
        abilityCast.timePast += (timePassedMillis * currentCooldownSpeed).roundToLong()
        abilityCast.timeLeftCooldown -= (timePassedMillis * currentCooldownSpeed).roundToLong()
    }
}
