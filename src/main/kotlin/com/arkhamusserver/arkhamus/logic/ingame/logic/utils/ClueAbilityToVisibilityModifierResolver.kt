package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability.*
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier.*
import org.springframework.stereotype.Component

@Component
class ClueAbilityToVisibilityModifierResolver {

    companion object {
        private val all = setOf(INSCRIPTION, SOUND, SCENT, AURA, CORRUPTION, OMEN, DISTORTION)
        private val allStrings = all.map { it.name }.toSet()
    }

    fun toVisibilityModifier(ability: Ability) =
        when (ability) {
            SEARCH_FOR_INSCRIPTION -> INSCRIPTION
            SEARCH_FOR_SOUND -> SOUND
            SEARCH_FOR_SCENT -> SCENT
            SEARCH_FOR_AURA -> AURA
            SEARCH_FOR_CORRUPTION -> CORRUPTION
            SEARCH_FOR_OMEN -> OMEN
            SEARCH_FOR_DISTORTION -> DISTORTION
            else -> null
        }

    fun allStrings(): Set<String> {
        return allStrings
    }
}