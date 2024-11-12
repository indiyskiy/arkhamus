package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.GameObjectFinder
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.GeometryUtils
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithId
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithPoint
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
        user: RedisGameUser,
        target: Any?,
        globalGameData: GlobalGameData
    ): Boolean {
        if (target == null) {
            logger.info("target is null")
            return false
        }
        if (target !is RedisGameUser) {
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
        user: RedisGameUser,
        globalGameData: GlobalGameData
    ): Boolean {
        return gameObjectFinder.all(
            ability.targetTypes ?: emptyList(),
            globalGameData
        ).any {
            (it is WithId && it.inGameId() != user.userId) &&
                    (it is WithPoint && geometryUtils.distanceLessOrEquals(user, it, ability.range))
        }
    }
}