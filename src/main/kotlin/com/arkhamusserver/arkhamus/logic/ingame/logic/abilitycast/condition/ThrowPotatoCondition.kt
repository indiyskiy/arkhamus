package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.GameObjectFinder
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.GeometryUtils
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.springframework.stereotype.Component

@Component
class ThrowPotatoCondition(
    private val geometryUtils: GeometryUtils,
    private val gameObjectFinder: GameObjectFinder
) : AdditionalAbilityCondition {

    override fun accepts(ability: Ability): Boolean {
        return ability == Ability.THROW_POTATO
    }

    override fun canBeCastedRightNow(
        ability: Ability,
        user: RedisGameUser,
        targetId: String?,
        targetType: GameObjectType?,
        globalGameData: GlobalGameData
    ): Boolean {
        if (targetId == null || targetType == null) return false
        val target = gameObjectFinder.findById(
            targetId,
            targetType,
            globalGameData
        )
        if (target == null || target !is RedisGameUser) return false
        return geometryUtils.distanceLessOrEquals(user, target, Ability.THROW_POTATO.range)
    }

    override fun canBeCastedAtAll(
        ability: Ability,
        user: RedisGameUser,
        globalGameData: GlobalGameData
    ): Boolean {
        return globalGameData.users.any {
            it.value.userId != user.userId &&
                    geometryUtils.distanceLessOrEquals(user, it.value, Ability.THROW_POTATO.range)
        }
    }
}