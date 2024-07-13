package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.GeometryUtils
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.Ability
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.springframework.stereotype.Component

@Component
class ThrowPotatoCondition(
    private val geometryUtils: GeometryUtils
) : AdditionalAbilityCondition {
    companion object {
        val MAX_DISTANCE: Double = 20.0
    }

    override fun accepts(ability: Ability): Boolean {
        return ability == Ability.THROW_POTATO
    }

    override fun fitCondition(
        ability: Ability,
        user: RedisGameUser,
        globalGameData: GlobalGameData
    ): Boolean {
        return globalGameData.users.any {
            it.value.userId != user.userId &&
                    geometryUtils.distanceLessOrEquals(user, it.value, MAX_DISTANCE)
        }
    }
}