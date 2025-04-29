package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ability

import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.ingame.InGameAbilityCooldown
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import org.springframework.stereotype.Component

@Component
class RelatedAbilityCastHandler {

    fun findCooldownsForUser(user: InGameUser, ability: Ability, abilityCooldown: List<InGameAbilityCooldown>) =
        if (ability.globalCooldown) {
            abilityCooldown.firstOrNull { it.ability == ability && it.timeLeftCooldown > 0 }
        } else {
            abilityCooldown.firstOrNull {
                it.ability == ability &&
                        it.sourceUserId == user.inGameId() &&
                        it.timeLeftCooldown > 0
            }
        }
}