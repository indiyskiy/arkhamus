package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ability

import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.ingame.InGameAbilityCast
import com.arkhamusserver.arkhamus.model.ingame.InGameGameUser
import org.springframework.stereotype.Component

@Component
class RelatedAbilityCastHandler {
    fun findForUser(
        user: InGameGameUser,
        ability: Ability,
        castAbilities: List<InGameAbilityCast>,
    ) =
        if (ability.globalCooldown) {
            castAbilities.firstOrNull { it.abilityId == ability.id && it.timeLeftCooldown > 0 }
        } else {
            castAbilities.firstOrNull {
                it.abilityId == ability.id &&
                        it.sourceUserId == user.inGameId() &&
                        it.timeLeftCooldown > 0
            }
        }
}