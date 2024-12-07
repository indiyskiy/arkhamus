package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.UserStateTag
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithStringId
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class KindleCloakAbilityCast : AbilityCast {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(KindleCloakAbilityCast::class.java)
    }

    override fun accept(ability: Ability): Boolean {
        return ability == Ability.KINDLE_CLOAK
    }

    override fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    ): Boolean {
        abilityRequestProcessData.gameUser?.let { luminosity(it) }
        return true
    }

    override fun cast(
        sourceUser: RedisGameUser,
        ability: Ability,
        target: WithStringId?,
        globalGameData: GlobalGameData
    ): Boolean {
        luminosity(sourceUser)
        return true
    }

    private fun luminosity(user: RedisGameUser): Boolean  {
        user.stateTags += UserStateTag.LUMINOUS
        return true
    }

}