package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition.searchclue

import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition.AdditionalAbilityCondition
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GameObjectFinder
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.parts.InGameSoundClueJammer
import org.springframework.stereotype.Component

@Component
class SearchForSoundAbilityCondition(
    private val userLocationHandler: UserLocationHandler,
    private val gameObjectFinder: GameObjectFinder
) : AdditionalAbilityCondition {

    override fun accepts(ability: Ability): Boolean =
        ability == Ability.SEARCH_FOR_SOUND

    override fun canBeCastedRightNow(
        ability: Ability,
        user: InGameUser,
        target: Any?,
        globalGameData: GlobalGameData
    ): Boolean {
        return target != null &&
              target is InGameSoundClueJammer &&
                userLocationHandler.userCanSeeTargetInRange(
                    user,
                    target,
                    globalGameData.levelGeometryData,
                    target.interactionRadius,
                    true
                ) && target.turnedOn
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
            it is InGameSoundClueJammer && userLocationHandler.userCanSeeTargetInRange(
                user,
                it,
                globalGameData.levelGeometryData,
                ability.range ?: 0.0,
                true
            ) && it.turnedOn
        }
    }

}