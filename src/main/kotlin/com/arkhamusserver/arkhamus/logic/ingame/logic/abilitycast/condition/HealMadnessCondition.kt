package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GameObjectFinder
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GeometryUtils
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.ingame.InGameGameUser
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithPoint
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithTrueIngameId
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class HealMadnessCondition(
    private val geometryUtils: GeometryUtils,
    private val gameObjectFinder: GameObjectFinder
) : AdditionalAbilityCondition {

    companion object {
        private val logger = LoggerFactory.getLogger(HealMadnessCondition::class.java)
    }

    override fun accepts(ability: Ability): Boolean {
        return ability == Ability.HEAL_MADNESS
    }

    override fun canBeCastedRightNow(
        ability: Ability,
        user: InGameGameUser,
        target: Any?,
        globalGameData: GlobalGameData
    ): Boolean {
        if (target == null) {
            logger.info("target is null")
            return false
        }
        if (target !is InGameGameUser) {
            logger.info("target is not user")
            return false
        }
        if (target.inGameId() == user.inGameId()) {
            logger.info("can't cast heal madness on myself")
            return false
        }
        return geometryUtils.distanceLessOrEquals(user, target, ability.range)
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