package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition.searchclue.v2

import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition.AdditionalAbilityCondition
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GameObjectFinder
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.clues.InGameDistortionClue
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithPoint
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class AdvancedSearchForDistortionAbilityCondition(
    private val userLocationHandler: UserLocationHandler,
    private val gameObjectFinder: GameObjectFinder
) : AdditionalAbilityCondition {

    companion object{
        private val logger = LoggerFactory.getLogger(AdvancedSearchForDistortionAbilityCondition::class.java)
    }

    override fun accepts(ability: Ability): Boolean =
        ability == Ability.ADVANCED_SEARCH_FOR_DISTORTION

    override fun canBeCastedRightNow(
        ability: Ability,
        user: InGameUser,
        target: Any?,
        globalGameData: GlobalGameData
    ): Boolean {
        if (target == null) {
            logger.warn("Target is null")
            return false
        }
        if (target !is InGameDistortionClue) {
            logger.warn("Target is not a distortion clue")
            return false
        }
        val canSeeAndInRange = userLocationHandler.userCanSeeTargetInRange(
            user,
            target,
            globalGameData.levelGeometryData,
            ability.range ?: 0.0,
            true
        )
        if (!canSeeAndInRange) {
            logger.warn("User cannot see target or target is out of range")
            return false
        }
        return true
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
            it is WithPoint && userLocationHandler.userCanSeeTargetInRange(
                user,
                it,
                globalGameData.levelGeometryData,
                ability.range ?: 0.0,
                true
            )
        }
    }

}