package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GameObjectFinder
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GeometryUtils
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability.THROW_POTATO
import com.arkhamusserver.arkhamus.model.ingame.InGameGameUser
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithPoint
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithTrueIngameId
import org.springframework.stereotype.Component

@Component
class ThrowPotatoCondition(
    private val geometryUtils: GeometryUtils,
    private val gameObjectFinder: GameObjectFinder
) : AdditionalAbilityCondition {

    override fun accepts(ability: Ability): Boolean {
        return ability == THROW_POTATO
    }

    override fun canBeCastedRightNow(
        ability: Ability,
        user: InGameGameUser,
        target: Any?,
        globalGameData: GlobalGameData
    ): Boolean {
        if (target == null) return false
        return geometryUtils.distanceLessOrEquals(user, target as WithPoint, ability.range)
    }

    override fun canBeCastedAtAll(
        ability: Ability,
        user: InGameGameUser,
        globalGameData: GlobalGameData
    ): Boolean {
        return gameObjectFinder.all(
            ability.targetTypes ?: emptyList(),
            globalGameData
        ).any {
            (it is WithTrueIngameId && it.inGameId() != user.inGameId()) &&
                    (it is WithPoint && geometryUtils.distanceLessOrEquals(user, it, ability.range))
        }
    }
}