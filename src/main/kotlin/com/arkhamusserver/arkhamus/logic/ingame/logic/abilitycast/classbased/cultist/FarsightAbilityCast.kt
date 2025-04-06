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
class FarsightAbilityCast() : AbilityCast {

    override fun accept(ability: Ability): Boolean {
        return ability == Ability.FARSIGHT
    }

    override fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    ): Boolean {
        farsight(abilityRequestProcessData)
        return true
    }

    override fun cast(
        sourceUser: InGameUser,
        ability: Ability,
        target: WithStringId?,
        globalGameData: GlobalGameData
    ): Boolean {
        farsight(sourceUser)
        return true
    }

    private fun farsight(
        abilityRequestProcessData: AbilityRequestProcessData,
    ) {
        val currentUser = abilityRequestProcessData.gameUser
        farsight(currentUser)
    }

    private fun farsight(currentUser: InGameUser?) {
        currentUser?.let {
            it.stateTags += UserStateTag.FARSIGHT
        }
    }
}