package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GameObjectFinder
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GeometryUtils
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.ingame.InGameContainer
import com.arkhamusserver.arkhamus.model.ingame.InGameCrafter
import com.arkhamusserver.arkhamus.model.ingame.InGameGameUser
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithPoint
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class LastPersonTouchCondition(
    private val geometryUtils: GeometryUtils,
    private val gameObjectFinder: GameObjectFinder
) : AdditionalAbilityCondition {

    companion object {
        private val logger = LoggerFactory.getLogger(LastPersonTouchCondition::class.java)
    }

    override fun accepts(ability: Ability): Boolean {
        return ability == Ability.TAKE_FINGERPRINTS
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
        if (target !is WithPoint) {
            logger.info("target is not WithPoint")
            return false
        }
        if (target !is InGameContainer && target !is InGameCrafter) {
            logger.info("target is not right")
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
            it is WithPoint && geometryUtils.distanceLessOrEquals(user, it, ability.range)
        }
    }
}