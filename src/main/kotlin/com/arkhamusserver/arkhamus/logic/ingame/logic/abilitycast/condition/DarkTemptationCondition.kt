package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GameObjectFinder
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GeometryUtils
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability.DARK_TEMPTATION
import com.arkhamusserver.arkhamus.model.ingame.InGameGameUser
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithPoint
import org.springframework.stereotype.Component

@Component
class DarkTemptationCondition(
    private val geometryUtils: GeometryUtils,
    private val gameObjectFinder: GameObjectFinder
) : AdditionalAbilityCondition {

    override fun accepts(ability: Ability): Boolean {
        return ability == DARK_TEMPTATION
    }

    override fun canBeCastedRightNow(
        ability: Ability,
        user: InGameGameUser,
        target: Any?,
        globalGameData: GlobalGameData
    ): Boolean {
        return target != null
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
            it is WithPoint && geometryUtils.distanceLessOrEquals(user, it, ability.range)
        }
    }
}