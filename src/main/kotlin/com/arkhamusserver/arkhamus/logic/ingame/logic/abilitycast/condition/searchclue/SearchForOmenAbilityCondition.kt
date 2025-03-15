package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition.searchclue

import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition.AdditionalAbilityCondition
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GameObjectFinder
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.clues.InGameOmenClue
import org.springframework.stereotype.Component

@Component
class SearchForOmenAbilityCondition(
    private val userLocationHandler: UserLocationHandler,
    private val gameObjectFinder: GameObjectFinder
) : AdditionalAbilityCondition {

    override fun accepts(ability: Ability): Boolean =
        ability == Ability.SEARCH_FOR_OMEN

    override fun canBeCastedRightNow(
        ability: Ability,
        user: InGameUser,
        target: Any?,
        globalGameData: GlobalGameData
    ): Boolean {
        return target != null &&
                target is InGameOmenClue &&
                with(globalGameData.users[target.userId]) {
                    this != null &&
                            userLocationHandler.userCanSeeTargetInRange(
                                user,
                                this,
                                globalGameData.levelGeometryData,
                                ability.range ?: 0.0,
                                true
                            ) &&
                            !target.castedAbilityUsers.contains(user.inGameId())
                }
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