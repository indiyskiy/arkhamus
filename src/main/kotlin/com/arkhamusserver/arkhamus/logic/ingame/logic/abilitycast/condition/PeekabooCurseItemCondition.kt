package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GameObjectFinder
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GeometryUtils
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability.PEEKABOO_CURSE_ITEM
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithPoint
import org.springframework.stereotype.Component

@Component
class PeekabooCurseItemCondition(
    private val geometryUtils: GeometryUtils,
    private val gameObjectFinder: GameObjectFinder
) : AdditionalAbilityCondition {

    override fun accepts(ability: Ability): Boolean {
        return ability == PEEKABOO_CURSE_ITEM
    }

    override fun canBeCastedRightNow(
        ability: Ability,
        user: RedisGameUser,
        target: Any?,
        globalGameData: GlobalGameData
    ): Boolean {
        return target != null
    }

    override fun canBeCastedAtAll(
        ability: Ability,
        user: RedisGameUser,
        globalGameData: GlobalGameData
    ): Boolean {
        return gameObjectFinder.all(
            ability.targetTypes ?: emptyList(),
            globalGameData
        ).any {
            it is WithPoint && geometryUtils.distanceLessOrEquals(user, it, ability.range)
        }
    }
}