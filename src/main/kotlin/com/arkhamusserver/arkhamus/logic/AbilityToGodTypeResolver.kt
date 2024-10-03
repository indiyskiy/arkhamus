package com.arkhamusserver.arkhamus.logic

import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.core.GodType
import org.springframework.stereotype.Component

@Component
class AbilityToGodTypeResolver {
    fun resolve(ability: Ability): GodType? {
        return when (ability) {
            Ability.SEARCH_FOR_INSCRIPTION -> GodType.INSCRIPTION
            Ability.SEARCH_FOR_SOUND -> GodType.SOUND
            Ability.SEARCH_FOR_SCENT -> GodType.SCENT
            Ability.SEARCH_FOR_AURA -> GodType.AURA
            Ability.SEARCH_FOR_CORRUPTION -> GodType.CORRUPTION
            Ability.SEARCH_FOR_OMEN -> GodType.OMEN
            Ability.SEARCH_FOR_DISTORTION -> GodType.DISTORTION
            else -> null
        }
    }

}