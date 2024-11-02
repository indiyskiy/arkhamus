package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.madness

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.AbilityCastHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.CanAbilityBeCastHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.GameObjectFinder
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.GeometryUtils
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.core.toAbility
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithPoint
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithStringId
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class CastSomethingMadnessLogic(
    private val canAbilityBeCastHandler: CanAbilityBeCastHandler,
    private val abilityCastHandler: AbilityCastHandler,
    private val userLocationHandler: UserLocationHandler,
    private val geometryUtils: GeometryUtils,
    private val finder: GameObjectFinder,
) {

    companion object {
        private val random = Random(System.currentTimeMillis())
    }

    fun castRandomSpell(user: RedisGameUser, data: GlobalGameData, timePassedMillis: Long): Boolean {
        val abilities = canAbilityBeCastHandler.abilityOfUserResponses(user, data)
            .filter {
                it.canBeCast &&
                        it.cooldown == 0L
            }
        if (abilities.isNotEmpty()) {
            val ability = abilities.random(random)
            val abilityType = ability.abilityId!!.toAbility()
            val targetNeeded = abilityType!!.targetTypes?.isNotEmpty() == true
            val target = findTarget(targetNeeded, abilityType, data, user)
            if (!targetNeeded || target != null) {
                abilityCastHandler.cast(user, abilityType, target, data)
            }
            return true
        }
        return false
    }

    private fun findTarget(
        targetNeeded: Boolean,
        abilityType: Ability,
        data: GlobalGameData,
        user: RedisGameUser
    ): WithStringId? = if (targetNeeded) {
        finder.all(abilityType.targetTypes!!, data).filter {
            it !is WithPoint || canSeeAndInRange(user, it, data, abilityType)
        }.random(random)
    } else null

    private fun canSeeAndInRange(
        user: RedisGameUser,
        point: WithPoint,
        data: GlobalGameData,
        abilityType: Ability
    ): Boolean = (userLocationHandler.userCanSeeTarget(
        user,
        point,
        data.levelGeometryData,
        true
    ) && geometryUtils.distanceLessOrEquals(user, point, abilityType.range)
            )
}