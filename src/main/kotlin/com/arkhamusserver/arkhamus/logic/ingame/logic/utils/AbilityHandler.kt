package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.redis.RedisAbilityCast
import org.springframework.stereotype.Component

@Component
class AbilityHandler {
    fun myActiveAbilities(userId: Long, castAbilities: List<RedisAbilityCast>): List<RedisAbilityCast> {
        return castAbilities.filter {
            userId == it.sourceUserId && it.state == RedisTimeEventState.ACTIVE
        }
    }
}