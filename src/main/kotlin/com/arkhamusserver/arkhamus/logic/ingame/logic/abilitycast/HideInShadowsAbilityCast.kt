package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.UserStateTag
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithStringId
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class HideInShadowsAbilityCast : AbilityCast {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(HideInShadowsAbilityCast::class.java)
    }

    override fun accept(ability: Ability): Boolean {
        return ability == Ability.HIDE_IN_SHADOWS
    }

    override fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    ): Boolean {
        abilityRequestProcessData.gameUser?.let {
            hideInShadows(it)
        }
        return true
    }

    override fun cast(
        sourceUser: InGameUser,
        ability: Ability,
        target: WithStringId?,
        globalGameData: GlobalGameData
    ): Boolean {
        hideInShadows(sourceUser)
        return true
    }

    private fun hideInShadows(user: InGameUser): Boolean {
        user.stateTags += UserStateTag.STEALTH
        return true
    }

}