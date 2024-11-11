package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithStringId
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class PretendCultistAbilityCast() : AbilityCast {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(PretendCultistAbilityCast::class.java)
    }

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
        user.visibilityModifiers.add(VisibilityModifier.PRETEND_CULTIST.name)
        return true
    }

    override fun cast(
        sourceUser: RedisGameUser,
        ability: Ability,
        target: WithStringId?,
        globalGameData: GlobalGameData
    ): Boolean {
        sourceUser.visibilityModifiers.add(VisibilityModifier.PRETEND_CULTIST.name)
        return true
    }

}