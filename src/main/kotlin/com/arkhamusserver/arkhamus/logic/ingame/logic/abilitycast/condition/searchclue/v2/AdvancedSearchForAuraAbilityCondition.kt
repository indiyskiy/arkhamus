package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition.searchclue.v2

import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition.AdditionalAbilityCondition
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GameObjectFinder
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.clues.InGameAuraClue
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class AdvancedSearchForAuraAbilityCondition(
    private val userLocationHandler: UserLocationHandler,
    private val gameObjectFinder: GameObjectFinder
) : AdditionalAbilityCondition {

    companion object{
        private val logger = LoggerFactory.getLogger(AdvancedSearchForAuraAbilityCondition::class.java)
    }

    override fun accepts(ability: Ability): Boolean =
        ability == Ability.SEARCH_FOR_AURA

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
        if (target !is InGameAuraClue) {
            logger.warn("Target is not a aura clue")
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
        if(target.castedAbilityUsers.contains(user.inGameId())){
            logger.info("User already activated searching for aura")
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
            it is InGameAuraClue && userLocationHandler.userCanSeeTargetInRange(
                user,
                it,
                globalGameData.levelGeometryData,
                ability.range ?: 0.0,
                true
            ) && !it.castedAbilityUsers.contains(user.inGameId())
        }
    }

}