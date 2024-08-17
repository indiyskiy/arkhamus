package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.enums.ingame.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.UserStateTag
import org.springframework.stereotype.Component

@Component
class FarsightAbilityCast() : AbilityCast {

    companion object {

    }

    override fun accept(ability: Ability): Boolean {
        return ability == Ability.FARSIGHT
    }

    override fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    ) {
        farsight(abilityRequestProcessData)
    }

    private fun farsight(
        abilityRequestProcessData: AbilityRequestProcessData,
    ) {
        val currentUser = abilityRequestProcessData.gameUser
        currentUser?.stateTags?.add(UserStateTag.FARSIGHT.name)
    }
}