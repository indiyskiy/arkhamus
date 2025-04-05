package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.usefullitems

import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.AbilityCast
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.DispellHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithStringId
import org.springframework.stereotype.Component

@Component
class ClearMindItemAbilityCast(
    private val dispellHandler: DispellHandler,
) : AbilityCast {
    override fun accept(ability: Ability): Boolean {
        return ability == Ability.CLEAR_MIND
    }

    override fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    ): Boolean {
        val target = abilityRequestProcessData.target
        dispellHandler.dispellPlayerOrNpc(target)
        return true
    }

    override fun cast(
        sourceUser: InGameUser,
        ability: Ability,
        target: WithStringId?,
        globalGameData: GlobalGameData
    ): Boolean {
        dispellHandler.dispellPlayerOrNpc(target)
        return true
    }

}