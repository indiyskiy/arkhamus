package com.arkhamusserver.arkhamus.logic.globalUtils

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings
import com.arkhamusserver.arkhamus.model.enums.ingame.InGameTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.TimeBase
import com.arkhamusserver.arkhamus.model.enums.ingame.TimeBase.*
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import org.springframework.stereotype.Component

@Component
class TimeBaseCalculator(
    private val globalGameSettings: GlobalGameSettings
) {

    fun resolveAbilityCooldown(ability: Ability): Long {
        return resolve(ability.timeBase(), ability.cooldown())
    }

    fun resolveAbilityActive(ability: Ability): Long? {
        return ability.active()?.let { resolve(ability.timeBase(), it) }
    }

    fun resolve(inGameTimeEventType: InGameTimeEventType): Long {
        return resolve(
            inGameTimeEventType.getTimeBase(),
            inGameTimeEventType.getTimeMultiplier()
        )
    }

    fun resolve(timeBase: TimeBase, timeMultiplier: Long): Long {
        val base = when (timeBase) {
            PLAIN_BASE -> 1L
            GAME_LENGTH_BASE -> globalGameSettings.gameLengthMinutes
            DAY_LENGTH_BASE -> globalGameSettings.dayLengthMinutes
            NIGHT_LENGTH_BASE -> globalGameSettings.nightLengthMinutes
        }
        return base * timeMultiplier
    }
}