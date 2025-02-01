package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.clues.ClueHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithStringId
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
        val target = abilityRequestProcessData.target
        if (target == null) return false
        cleanUpClue(
            globalGameData,
            target
        )
        return true
    }

    override fun cast(
        sourceUser: InGameUser,
        ability: Ability,
        target: WithStringId?,
        globalGameData: GlobalGameData
    ): Boolean {
        if (target == null) return false
        cleanUpClue(globalGameData, target)
        return true
    }

    private fun cleanUpClue(
        data: GlobalGameData,
        target: WithStringId
    ) {
        clueHandler.removeClue(
            data,
            target
        )
    }

}