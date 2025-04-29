package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.UserStateTag
import com.arkhamusserver.arkhamus.model.ingame.InGameAbilityActiveCast
import org.springframework.stereotype.Component

@Component
class HideInShadowsAbilityProcessor(
    private val userLocationHandler: UserLocationHandler,
) : ActiveAbilityProcessor {

    override fun accepts(castAbility: InGameAbilityActiveCast): Boolean {
        return castAbility.ability == Ability.HIDE_IN_SHADOWS
    }

    override fun processActive(
        castAbility: InGameAbilityActiveCast,
        globalGameData: GlobalGameData
    ) {
        val user = globalGameData.users[castAbility.sourceUserId]
        if (user == null) {
            endAbility(castAbility)
            return
        }
        if (!userLocationHandler.isInDarkness(user, globalGameData)) {
            user.stateTags -= UserStateTag.STEALTH
            endAbility(castAbility)
        }
    }

    private fun endAbility(
        cast: InGameAbilityActiveCast,
    ) {
        cast.timeLeftActive = 0
    }

    override fun finishActive(castAbility: InGameAbilityActiveCast, globalGameData: GlobalGameData) {
        val user = globalGameData.users[castAbility.sourceUserId]
        if (user == null) return
        user.stateTags -= UserStateTag.STEALTH
    }

}


