package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.clues.ClueHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability.CLEAN_UP_CLUE
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import org.springframework.stereotype.Component

@Component
class CleanUpClueCondition(
    private val clueHandler: ClueHandler
) : AdditionalAbilityCondition {

    companion object{
        private val logger = LoggingUtils.getLogger<CleanUpClueCondition>()
    }

    override fun accepts(ability: Ability): Boolean {
        return ability == CLEAN_UP_CLUE
    }

    override fun canBeCastedRightNow(
        ability: Ability,
        user: InGameUser,
        target: Any?,
        globalGameData: GlobalGameData
    ): Boolean {
        if (target == null) return false
        val result =  clueHandler.canBeRemovedByAbility(user, target, globalGameData)
        logger.info("can we cast CLEAN_UP_CLUE ability to target: $target ? $result")
        return result
    }

    override fun canBeCastedAtAll(
        ability: Ability,
        user: InGameUser,
        globalGameData: GlobalGameData
    ): Boolean {
        return clueHandler.anyCanBeRemovedByAbility(user, globalGameData)
    }
}