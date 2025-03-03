package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.classbased.cultist

import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.AbilityCast
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.UserStateTag
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithStringId
import org.springframework.stereotype.Component

@Component
class MadnessLinkAbilityCast() : AbilityCast {

    override fun accept(ability: Ability): Boolean {
        return ability == Ability.MADNESS_LINK
    }

    override fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    ): Boolean {
        linkMadness(abilityRequestProcessData)
        return true
    }

    override fun cast(
        sourceUser: InGameUser,
        ability: Ability,
        target: WithStringId?,
        globalGameData: GlobalGameData
    ): Boolean {
        linkMadness(target as InGameUser, sourceUser)
        return true
    }

    private fun linkMadness(
        abilityRequestProcessData: AbilityRequestProcessData,
    ) {
        val currentUser = abilityRequestProcessData.gameUser
        val targetUser = abilityRequestProcessData.target as InGameUser

        linkMadness(targetUser, currentUser)
    }

    private fun linkMadness(
        targetUser: InGameUser,
        currentUser: InGameUser?
    ) {
        currentUser?.let {
            it.stateTags + UserStateTag.MADNESS_LINK_SOURCE
        }
        targetUser.stateTags + UserStateTag.MADNESS_LINK_TARGET
    }
}