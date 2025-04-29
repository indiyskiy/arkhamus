package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors.cultist

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors.ActiveAbilityProcessor
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.UserStateTag
import com.arkhamusserver.arkhamus.model.ingame.InGameAbilityActiveCast
import org.springframework.stereotype.Component

@Component
class FarsightAbilityProcessor : ActiveAbilityProcessor {

    override fun accepts(castAbility: InGameAbilityActiveCast): Boolean {
        return castAbility.ability == Ability.FARSIGHT
    }

    override fun processActive(
        castAbility: InGameAbilityActiveCast, globalGameData: GlobalGameData
    ) {

    }

    override fun finishActive(castAbility: InGameAbilityActiveCast, globalGameData: GlobalGameData) {
        val user = globalGameData.users[castAbility.sourceUserId]
        if (user != null) {
            user.stateTags -= UserStateTag.FARSIGHT
        }
    }

}


