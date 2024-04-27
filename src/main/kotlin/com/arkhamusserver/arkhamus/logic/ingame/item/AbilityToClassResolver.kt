package com.arkhamusserver.arkhamus.logic.ingame.item

import com.arkhamusserver.arkhamus.model.enums.ingame.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.Ability.HEAL_MADNESS
import com.arkhamusserver.arkhamus.model.enums.ingame.ClassInGame
import com.arkhamusserver.arkhamus.model.enums.ingame.ClassInGame.MIND_HEALER
import org.springframework.stereotype.Component

@Component
class AbilityToClassResolver {
    fun resolve(ability: Ability): Set<ClassInGame>? =
        if (!ability.classBased) {
            null
        } else {
            when (ability) {
                HEAL_MADNESS -> setOf(MIND_HEALER)
                else -> null
            }
        }
}

