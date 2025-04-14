package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition.classbased.cultist

import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.TargetableUtils
import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition.AdditionalAbilityCondition
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GameObjectFinder
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.UserStateTag
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import org.springframework.stereotype.Component

@Component
class ParalyzeAbilityCondition(
    private val userLocationHandler: UserLocationHandler,
    private val gameObjectFinder: GameObjectFinder,
    private val targetableUtils: TargetableUtils
) : AdditionalAbilityCondition {

    override fun accepts(ability: Ability): Boolean {
        return ability == Ability.PARALYSE
    }

    override fun canBeCastedRightNow(
        ability: Ability,
        user: InGameUser,
        target: Any?,
        globalGameData: GlobalGameData
    ): Boolean {
        if (target == null) return false
        val targetUser = (target as? InGameUser) ?: return false
        return targetUser.inGameId() != user.inGameId() &&
                targetableUtils.isTargetable(user) &&
                userLocationHandler.userCanSeeTargetInRange(
                    user,
                    targetUser,
                    globalGameData.levelGeometryData,
                    ability.range ?: 0.0,
                    true
                ) &&
        !targetUser.stateTags.contains(UserStateTag.STUN)
    }

    override fun canBeCastedAtAll(
        ability: Ability,
        user: InGameUser,
        globalGameData: GlobalGameData
    ): Boolean {
        return gameObjectFinder.all(
            ability.targetTypes ?: emptyList(),
            globalGameData
        ).any {
            canBeCastedRightNow(
                ability,
                user,
                it,
                globalGameData
            )
        }
    }
}