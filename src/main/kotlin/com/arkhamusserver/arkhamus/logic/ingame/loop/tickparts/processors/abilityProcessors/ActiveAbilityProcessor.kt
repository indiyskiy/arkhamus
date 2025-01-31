package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.ingame.InGameAbilityCast

interface ActiveAbilityProcessor {
    fun accepts(castAbility: InGameAbilityCast): Boolean
    fun processActive(castAbility: InGameAbilityCast, globalGameData: GlobalGameData)
    fun finishActive(castAbility: InGameAbilityCast, globalGameData: GlobalGameData)
}