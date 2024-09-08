package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.Ability
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.springframework.stereotype.Component

@Component
class TownPortalByScrollCondition(
) : AdditionalAbilityCondition {

    override fun accepts(ability: Ability): Boolean {
        return ability == Ability.TOWN_PORTAL_BY_SCROLL
    }

    override fun fitCondition(
        ability: Ability,
        user: RedisGameUser,
        globalGameData: GlobalGameData
    ): Boolean {
        return true
    }
}