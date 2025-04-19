package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.usefullitems

import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.AbilityCast
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithStringId
import org.springframework.stereotype.Component

@Component
class PretendCultistAbilityCast() : AbilityCast {

    override fun accept(ability: Ability): Boolean {
        return ability == Ability.PRETEND_CULTIST
    }

    override fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    ): Boolean {
        val user = abilityRequestProcessData.gameUser
        if (user == null) return false
        user.visibilityModifiers += VisibilityModifier.PRETEND_CULTIST
        return true
    }

    override fun cast(
        sourceUser: InGameUser,
        ability: Ability,
        target: WithStringId?,
        globalGameData: GlobalGameData
    ): Boolean {
        sourceUser.visibilityModifiers += VisibilityModifier.PRETEND_CULTIST
        return true
    }

}