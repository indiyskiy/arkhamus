package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition.usefullitems

import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.TargetableUtils
import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition.AdditionalAbilityCondition
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GameObjectFinder
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import org.springframework.stereotype.Component

@Component
class HealMadnessByPillCondition(
    private val gameObjectFinder: GameObjectFinder,
    private val userLocationHandler: UserLocationHandler,
    private val targetableUtils: TargetableUtils
) : AdditionalAbilityCondition {

    override fun accepts(ability: Ability): Boolean {
        return ability == Ability.HEAL_MADNESS_BY_PILL
    }

    override fun canBeCastedRightNow(
        ability: Ability,
        user: InGameUser,
        target: Any?,
        globalGameData: GlobalGameData
    ): Boolean {
        if (target == null) {
            return false
        }
        if (target !is InGameUser) {
            return false
        }
        if (target.inGameId() == user.inGameId()) {
            return false
        }
        if(!targetableUtils.isTargetable(user)) return false
        return userLocationHandler.userCanSeeTargetInRange(
            user,
            target,
            globalGameData.levelGeometryData,
            ability.range ?: 0.0,
            true
        )
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