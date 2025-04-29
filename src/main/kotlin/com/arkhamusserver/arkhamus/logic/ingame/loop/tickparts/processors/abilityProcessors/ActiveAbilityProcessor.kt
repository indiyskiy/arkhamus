package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.ingame.InGameAbilityActiveCast

interface ActiveAbilityProcessor {
    fun accepts(castAbility: InGameAbilityActiveCast): Boolean
    fun processActive(castAbility: InGameAbilityActiveCast, globalGameData: GlobalGameData)
    fun finishActive(castAbility: InGameAbilityActiveCast, globalGameData: GlobalGameData)
}