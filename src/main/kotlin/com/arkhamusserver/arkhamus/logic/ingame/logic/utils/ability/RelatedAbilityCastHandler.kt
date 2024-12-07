package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ability

import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.redis.RedisAbilityCast
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.springframework.stereotype.Component

@Component
class RelatedAbilityCastHandler {
    fun findForUser(
        user: RedisGameUser,
        ability: Ability,
        castAbilities: List<RedisAbilityCast>,
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