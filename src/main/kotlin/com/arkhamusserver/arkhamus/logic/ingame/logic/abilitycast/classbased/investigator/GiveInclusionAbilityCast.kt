package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.classbased.investigator

import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.AbilityCast
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.UserStateTag
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithStringId
import org.springframework.stereotype.Component

@Component
class GiveInclusionAbilityCast() : AbilityCast {

    override fun accept(ability: Ability): Boolean {
        return ability == Ability.GIVE_INCLUSION
    }

    override fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    ): Boolean {
        giveInclusion(abilityRequestProcessData)
        return true
    }

    override fun cast(
        sourceUser: InGameUser,
        ability: Ability,
        target: WithStringId?,
        globalGameData: GlobalGameData
    ): Boolean {
        giveInclusion(target as InGameUser)
        return true
    }

    private fun giveInclusion(
        abilityRequestProcessData: AbilityRequestProcessData,
    ) {
        val targetUser = abilityRequestProcessData.target as InGameUser
        giveInclusion(targetUser)
    }

    private fun giveInclusion(
        targetUser: InGameUser,
    ) {
        targetUser.stateTags += UserStateTag.HAVE_INCLUSION
    }
}