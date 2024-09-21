package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser

interface AdditionalAbilityCondition {
    fun accepts(ability: Ability): Boolean

    fun canBeCastedRightNow(
        ability: Ability,
        user: RedisGameUser,
        targetId: String?,
        targetType: GameObjectType?,
        globalGameData: GlobalGameData
    ): Boolean

    fun canBeCastedAtAll(
        ability: Ability,
        user: RedisGameUser,
        globalGameData: GlobalGameData
    ): Boolean
}