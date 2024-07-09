package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast

import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition.AdditionalHealMadnessCondition
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.GeometryUtils
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserMadnessHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.enums.ingame.Ability
import org.springframework.stereotype.Component

@Component
class HealMadnessAbilityCast(
    private val geometryUtils: GeometryUtils,
    private val madnessHandler: UserMadnessHandler
) : AbilityCast {

    private val REDUCE_VALUE: Double = 20.0

    override fun accept(ability: Ability): Boolean {
        return ability == Ability.HEAL_MADNESS
    }

    override fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    ) {
        healMadness(globalGameData, abilityRequestProcessData)
    }

    private fun healMadness(
        globalGameData: GlobalGameData,
        abilityRequestProcessData: AbilityRequestProcessData,
    ) {
        val currentUser = abilityRequestProcessData.gameUser
        currentUser?.let { currentUserNotNull ->
            val user = globalGameData.users.values.filter {
                it.userId != abilityRequestProcessData.gameUser.userId
            }.minByOrNull { user ->
                geometryUtils.distance(
                    currentUserNotNull.x,
                    currentUserNotNull.y,
                    user.x,
                    user.y
                )
            }
            if (user != null && geometryUtils.distanceLessOrEquals(
                    currentUserNotNull,
                    user,
                    AdditionalHealMadnessCondition.MAX_DISTANCE
                )
            ) {
                madnessHandler.reduceMadness(user, REDUCE_VALUE)
            }
        }
    }
}