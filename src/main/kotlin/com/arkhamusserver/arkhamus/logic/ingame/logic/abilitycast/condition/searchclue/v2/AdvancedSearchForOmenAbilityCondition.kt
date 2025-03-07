package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition.searchclue.v2

import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition.AdditionalAbilityCondition
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GameObjectFinder
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.clues.InGameOmenClue
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class AdvancedSearchForOmenAbilityCondition(
    private val userLocationHandler: UserLocationHandler,
    private val gameObjectFinder: GameObjectFinder
) : AdditionalAbilityCondition {

    companion object {
        private val logger = LoggerFactory.getLogger(AdvancedSearchForOmenAbilityCondition::class.java)
    }

    override fun accepts(ability: Ability): Boolean =
        ability == Ability.SEARCH_FOR_OMEN

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
        if (target !is InGameOmenClue) {
            logger.warn("Target is not a omen clue")
            return false
        }
        val targetUser = globalGameData.users[target.userId]
        if (targetUser == null) {
            logger.warn("Related for omen user not found")
            return false
        }
        val canSeeAndInRange = userLocationHandler.userCanSeeTargetInRange(
            user,
            targetUser,
            globalGameData.levelGeometryData,
            ability.range ?: 0.0,
            true
        )
        if (!canSeeAndInRange) {
            logger.warn("User cannot see target or target is out of range")
            return false
        }
        if (target.castedAbilityUsers.contains(user.inGameId())) {
            logger.warn("ability already casted")
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
            if (it !is InGameOmenClue) {
                false
            } else {
                val targetUser = globalGameData.users[it.userId]
                targetUser != null && userLocationHandler.userCanSeeTargetInRange(
                    user,
                    targetUser,
                    globalGameData.levelGeometryData,
                    ability.range ?: 0.0,
                    true
                ) && !it.castedAbilityUsers.contains(user.inGameId())
            }
        }
    }

}