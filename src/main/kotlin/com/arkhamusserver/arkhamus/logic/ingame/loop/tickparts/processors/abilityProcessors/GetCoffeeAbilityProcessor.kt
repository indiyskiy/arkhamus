package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.UserStateTag
import com.arkhamusserver.arkhamus.model.ingame.InGameAbilityCast
import org.springframework.stereotype.Component

@Component
class GetCoffeeAbilityProcessor : ActiveAbilityProcessor {

    override fun accepts(castAbility: InGameAbilityCast): Boolean {
        return castAbility.ability == Ability.GET_COFFEE
    }

    override fun processActive(
        castAbility: InGameAbilityCast,
        globalGameData: GlobalGameData
    ) {

    }

    override fun finishActive(castAbility: InGameAbilityCast, globalGameData: GlobalGameData) {
        val user = globalGameData.users[castAbility.sourceUserId]
        if (user == null) return
        user.stateTags -= UserStateTag.COFFEINE_RUSH
    }

}


