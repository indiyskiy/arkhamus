package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.usefullitems

import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.AbilityCast
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserMadnessHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithStringId
import org.springframework.stereotype.Component

@Component
class HealMadnessByPillAbilityCast(
    private val madnessHandler: UserMadnessHandler
) : AbilityCast {

    companion object {
        private const val REDUCE_VALUE: Double = 20.0
    }

    override fun accept(ability: Ability): Boolean {
        return ability == Ability.HEAL_MADNESS_BY_PILL
    }

    override fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    ): Boolean {
        healMadness(abilityRequestProcessData)
        return true
    }

    override fun cast(
        sourceUser: InGameUser,
        ability: Ability,
        target: WithStringId?,
        globalGameData: GlobalGameData
    ): Boolean {
        healMadness(target as InGameUser)
        return true
    }

    private fun healMadness(
        abilityRequestProcessData: AbilityRequestProcessData,
    ) {
        val target = abilityRequestProcessData.target as InGameUser
        healMadness(target)
    }

    private fun healMadness(
        target: InGameUser
    ) {
        madnessHandler.reduceMadness(target, REDUCE_VALUE)
    }

}