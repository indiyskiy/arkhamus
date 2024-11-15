package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.madness

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ability.AbilityCastHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ability.CanAbilityBeCastHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GameObjectFinder
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GameObjectFinder.TypedGameObject
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GeometryUtils
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.core.toAbility
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithPoint
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

    fun castRandomSpell(user: RedisGameUser, data: GlobalGameData): Boolean {
        val abilities = canAbilityBeCastHandler.abilityOfUserResponses(user, data)
            .filter {
                it.canBeCast &&
                        it.cooldown == 0L
            }
        if (abilities.isNotEmpty()) {
            val ability = abilities.random(random)
            val abilityType = ability.abilityId!!.toAbility()
            val targetNeeded = abilityType!!.targetTypes?.isNotEmpty() == true
            val targetTyped = findTarget(targetNeeded, abilityType, data, user)
            val target = targetTyped?.gameObject
            val targetType = targetTyped?.type
            if (!targetNeeded || target != null) {
                abilityCastHandler.cast(
                    user,
                    abilityType,
                    target,
                    data,
                    targetType
                )
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
    ): TypedGameObject? = if (targetNeeded) {
        val allTyped = finder.allTyped(abilityType.targetTypes!!, data).filter {
            val gameObject = it.gameObject
            gameObject !is WithPoint || canSeeAndInRange(user, gameObject, data, abilityType)
        }
        if (allTyped.isNotEmpty()) {
            allTyped.random(random)
        } else {
            null
        }
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