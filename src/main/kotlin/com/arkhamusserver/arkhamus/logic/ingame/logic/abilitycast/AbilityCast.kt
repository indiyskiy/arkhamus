package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.enums.ingame.Ability

interface AbilityCast {
    fun accept(ability: Ability): Boolean
    fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    )
}