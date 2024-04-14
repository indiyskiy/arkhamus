package com.arkhamusserver.arkhamus.logic.ingame.item

import com.arkhamusserver.arkhamus.model.enums.ingame.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.ClassInGame
import org.springframework.stereotype.Component

@Component
class AbilityToClassResolver {
    fun resolve(ability: Ability): ClassInGame? =
        if (!ability.classBased) {
            null
        } else {
            when (ability) {
                Ability.HEAL_MADNESS -> ClassInGame.MIND_HEALER
                else -> null
            }
        }
}

