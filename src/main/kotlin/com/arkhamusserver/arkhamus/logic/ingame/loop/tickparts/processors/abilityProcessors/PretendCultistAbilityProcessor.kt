package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.ingame.InGameAbilityActiveCast
import org.springframework.stereotype.Component

@Component
class PretendCultistAbilityProcessor() : ActiveAbilityProcessor {

    override fun accepts(castAbility: InGameAbilityActiveCast): Boolean {
        return castAbility.ability == Ability.PRETEND_CULTIST
    }

    override fun processActive(
        castAbility: InGameAbilityActiveCast,
        globalGameData: GlobalGameData
    ) {

    }

    override fun finishActive(castAbility: InGameAbilityActiveCast, globalGameData: GlobalGameData) {
        val user = globalGameData.users[castAbility.sourceUserId]
        if (user == null) return
        user.visibilityModifiers -= VisibilityModifier.PRETEND_CULTIST
    }
}

