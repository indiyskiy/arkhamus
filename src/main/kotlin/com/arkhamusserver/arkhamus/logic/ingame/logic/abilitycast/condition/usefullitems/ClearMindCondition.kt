package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition.usefullitems

import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition.AdditionalAbilityCondition
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GameObjectFinder
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability.CLEAR_MIND
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithPoint
import org.springframework.stereotype.Component

@Component
class ClearMindCondition(
    private val gameObjectFinder: GameObjectFinder,
    private val userLocationHandler: UserLocationHandler,
) : AdditionalAbilityCondition {

    override fun accepts(ability: Ability): Boolean {
        return ability == CLEAR_MIND
    }

    override fun canBeCastedRightNow(
        ability: Ability,
        user: InGameUser,
        target: Any?,
        globalGameData: GlobalGameData
    ): Boolean {
        return target != null &&
                target is WithPoint &&
                userLocationHandler.userCanSeeTargetInRange(
                    user,
                    target,
                    globalGameData.levelGeometryData,
                    ability.range ?: 0.0,
                    true
                ) &&
                (target !is InGameUser || target.inGameId() != user.inGameId())
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