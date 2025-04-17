package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.usefullitems

import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.AbilityCast
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
class GetCoffeeAbilityCast() : AbilityCast {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(GetCoffeeAbilityCast::class.java)
    }

    override fun accept(ability: Ability): Boolean {
        return ability == Ability.GET_COFFEE
    }

    override fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    ): Boolean {
        val user = abilityRequestProcessData.gameUser
        if (user == null) return false
        user.stateTags += UserStateTag.COFFEINE_RUSH
        return true
    }

    override fun cast(
        sourceUser: InGameUser,
        ability: Ability,
        target: WithStringId?,
        globalGameData: GlobalGameData
    ): Boolean {
        sourceUser.stateTags += UserStateTag.COFFEINE_RUSH
        return true
    }

}