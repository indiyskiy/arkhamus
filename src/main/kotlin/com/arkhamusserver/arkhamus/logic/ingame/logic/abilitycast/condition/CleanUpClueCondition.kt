package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.clues.ClueHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GameObjectFinder
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability.CLEAN_UP_CLUE
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class CleanUpClueCondition(
    private val gameObjectFinder: GameObjectFinder,
    private val userLocationHandler: UserLocationHandler,
    private val clueHandler: ClueHandler
) : AdditionalAbilityCondition {

    companion object {
        private val logger = LoggerFactory.getLogger(CleanUpClueCondition::class.java)
    }

    override fun accepts(ability: Ability): Boolean {
        return ability == CLEAN_UP_CLUE
    }

    override fun canBeCastedRightNow(
        ability: Ability,
        user: RedisGameUser,
        target: Any?,
        globalGameData: GlobalGameData
    ): Boolean {
        if (target == null) return false
        val canBeRemoved = clueHandler.canBeRemoved(user, target, globalGameData)
        return canBeRemoved
    }

    override fun canBeCastedAtAll(
        ability: Ability,
        user: RedisGameUser,
        globalGameData: GlobalGameData
    ): Boolean {
        return clueHandler.anyCanBeRemoved(user, globalGameData)
    }
}