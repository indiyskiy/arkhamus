package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import org.springframework.stereotype.Component

@Component
class ClueAbilityToVisibilityModifierResolver {
    fun toVisibilityModifier(ability: Ability) =
        when (ability) {
            Ability.SEARCH_FOR_INSCRIPTION -> VisibilityModifier.INSCRIPTION
            Ability.SEARCH_FOR_SOUND -> VisibilityModifier.SOUND
            Ability.SEARCH_FOR_SCENT -> VisibilityModifier.SCENT
            Ability.SEARCH_FOR_AURA -> VisibilityModifier.AURA
            Ability.SEARCH_FOR_CORRUPTION -> VisibilityModifier.CORRUPTION
            Ability.SEARCH_FOR_OMEN -> VisibilityModifier.OMEN
            Ability.SEARCH_FOR_DISTORTION -> VisibilityModifier.DISTORTION
            else -> null
        }
}