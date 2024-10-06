package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ClueHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.redis.RedisClue
import org.springframework.stereotype.Component

@Component
class CleanUpClueAbilityCast(
    private val clueHandler: ClueHandler
) : AbilityCast {
    override fun accept(ability: Ability): Boolean {
        return ability == Ability.CLEAN_UP_CLUE
    }

    override fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    ): Boolean {
        cleanUpClue(abilityRequestProcessData)
        return true
    }

    private fun cleanUpClue(
        abilityData: AbilityRequestProcessData,
    ) {
        val target = abilityData.target
        if (target != null) {
            clueHandler.removeClue(target as RedisClue)
        }
    }

}