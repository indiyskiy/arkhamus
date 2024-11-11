package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.GameObjectFinder
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.GeometryUtils
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability.GREAT_DISPELL
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithPoint
import org.springframework.stereotype.Component

@Component
class GreatDispellCondition(
    private val geometryUtils: GeometryUtils,
    private val gameObjectFinder: GameObjectFinder
) : AdditionalAbilityCondition {

    override fun accepts(ability: Ability): Boolean {
        return ability == GREAT_DISPELL
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