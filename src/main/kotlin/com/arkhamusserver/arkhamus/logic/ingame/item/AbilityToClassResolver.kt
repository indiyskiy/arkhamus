package com.arkhamusserver.arkhamus.logic.ingame.item

import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability.*
import com.arkhamusserver.arkhamus.model.enums.ingame.core.ClassInGame
import com.arkhamusserver.arkhamus.model.enums.ingame.core.ClassInGame.*
import org.springframework.stereotype.Component

@Component
class AbilityToClassResolver {
    fun resolve(ability: Ability): Set<ClassInGame>? =
        if (!ability.classBased) {
            null
        } else {
            when (ability) {
                //investigators
                HEAL_MADNESS -> setOf(MIND_HEALER)
                SPAWN_LOOT -> setOf(BREADWINNER)
                TAKE_FINGERPRINTS -> setOf(FORENSIC_SCIENTIST)
                //cultists
                FARSIGHT -> setOf(ARISTOCRAT)
                PARALYSE -> setOf(DESCENDANT)
                MADNESS_LINK -> setOf(MADNESS_SHIFTER)
                else -> null
            }
        }
}

