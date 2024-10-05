package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.UserStateTag
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import org.springframework.stereotype.Component

@Component
class SeeTheOmenAbilityCast() : AbilityCast {

    companion object {
        private val visibilityModifiers = listOf(
            VisibilityModifier.INSCRIPTION,
            VisibilityModifier.SOUND,
            VisibilityModifier.SCENT,
            VisibilityModifier.AURA,
            VisibilityModifier.CORRUPTION,
            VisibilityModifier.OMEN,
            VisibilityModifier.DISTORTION,
        )
    }

    override fun accept(ability: Ability): Boolean {
        return ability == Ability.SEE_THE_OMEN
    }

    override fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    ): Boolean {
        seeTheOmen(abilityRequestProcessData)
        return true
    }

    private fun seeTheOmen(
        abilityRequestProcessData: AbilityRequestProcessData,
    ) {
        val currentUser = abilityRequestProcessData.gameUser
        currentUser?.let { currentUserNotNull ->
            currentUserNotNull.stateTags.add(UserStateTag.INVESTIGATING.name)
            currentUserNotNull.rewriteVisibilityModifiers(
                currentUserNotNull.visibilityModifiers()
                    .plus(visibilityModifiers)
                    .distinct()
            )
        }
    }
}