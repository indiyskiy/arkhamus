package com.arkhamusserver.arkhamus.logic.ingame.item

import com.arkhamusserver.arkhamus.model.enums.ingame.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.Ability.*
import com.arkhamusserver.arkhamus.model.enums.ingame.ClassInGame
import com.arkhamusserver.arkhamus.model.enums.ingame.ClassInGame.*
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
                //cultists
                FARSIGHT -> setOf(ARISTOCRAT)
                SEE_THE_OMEN -> setOf(DESCENDANT)
                else -> null
            }
        }
}

