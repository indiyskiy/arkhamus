package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition.searchclue.v2

import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition.AdditionalAbilityCondition
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GameObjectFinder
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.parts.InGameSoundClueJammer
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class AdvancedSearchForSoundAbilityCondition(
    private val userLocationHandler: UserLocationHandler,
    private val gameObjectFinder: GameObjectFinder
) : AdditionalAbilityCondition {

    companion object{
        private val logger = LoggerFactory.getLogger(AdvancedSearchForSoundAbilityCondition::class.java)
    }

    override fun accepts(ability: Ability): Boolean =
        ability == Ability.ADVANCED_SEARCH_FOR_SOUND

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
        if (target !is InGameSoundClueJammer) {
            logger.warn("Target is not a sound clue jammer")
            return false
        }
        val canSeeAndInRange = userLocationHandler.userCanSeeTargetInRange(
            user,
            target,
            globalGameData.levelGeometryData,
            target.interactionRadius,
            true
        )
        if (!canSeeAndInRange) {
            logger.warn("User cannot see target or target is out of range")
            return false
        }
        if (!target.turnedOn){
            logger.warn("jammer is not turned on")
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