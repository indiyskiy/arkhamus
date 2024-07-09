package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.Ability
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser

interface AdditionalAbilityCondition {
    fun accepts(ability: Ability): Boolean
    fun fitCondition(ability: Ability, user: RedisGameUser, globalGameData: GlobalGameData): Boolean
}