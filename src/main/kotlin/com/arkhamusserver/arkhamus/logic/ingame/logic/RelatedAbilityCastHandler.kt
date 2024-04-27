package com.arkhamusserver.arkhamus.logic.ingame.logic

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.Ability
import com.arkhamusserver.arkhamus.model.redis.RedisAbilityCast
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.AbilityOfUserResponse
import org.springframework.stereotype.Component

@Component
class RelatedAbilityCastHandler {
    fun findForUser(user: RedisGameUser, ability: Ability, castedAbilities: List<RedisAbilityCast>) =
         if (ability.globalCooldown) {
            castedAbilities.firstOrNull { it.abilityId == ability.id && it.timeLeft > 0 }
        } else {
            castedAbilities.firstOrNull {
                it.abilityId == ability.id &&
                        it.sourceUserId == user.userId &&
                        it.timeLeft > 0
            }
        }
}