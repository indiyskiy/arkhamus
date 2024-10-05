package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.GameObjectFinder
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.GeometryUtils
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability.PEEKABOO_CURSE_ITEM
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithPoint
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class PeekabooCurseItemCondition(
    private val geometryUtils: GeometryUtils,
    private val gameObjectFinder: GameObjectFinder
) : AdditionalAbilityCondition {

    companion object {
        private val logger = LoggerFactory.getLogger(PeekabooCurseItemCondition::class.java)
    }

    override fun accepts(ability: Ability): Boolean {
        return ability == PEEKABOO_CURSE_ITEM
    }

    override fun canBeCastedRightNow(
        ability: Ability,
        user: RedisGameUser,
        target: Any?,
        globalGameData: GlobalGameData
    ): Boolean {
        if (target == null) return false
        val canBeCasted = geometryUtils.distanceLessOrEquals(user, target as WithPoint, ability.range)
        logger.info("canBeCasted: $canBeCasted")
        return canBeCasted
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