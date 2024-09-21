package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.enums.ingame.UserStateTag
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.springframework.stereotype.Component

@Component
class KindleCloakCondition : AdditionalAbilityCondition {

    override fun accepts(ability: Ability): Boolean {
        return ability == Ability.KINDLE_CLOAK
    }

    override fun canBeCastedRightNow(
        ability: Ability,
        user: RedisGameUser,
        targetId: String?,
        targetType: GameObjectType?,
        globalGameData: GlobalGameData
    ): Boolean {
        return canBeCastedAtAll(
            ability,
            user,
            globalGameData)
    }

    override fun canBeCastedAtAll(
        ability: Ability,
        user: RedisGameUser,
        globalGameData: GlobalGameData
    ): Boolean {
        return !user.stateTags.contains(UserStateTag.LUMINOUS.name)
    }
}