package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.redis.RedisAbilityCast

interface ActiveAbilityProcessor {
    fun accepts(castedAbility: RedisAbilityCast): Boolean
    fun processActive(castedAbility: RedisAbilityCast, globalGameData: GlobalGameData)
    fun finishActive(castedAbility: RedisAbilityCast, globalGameData: GlobalGameData)
}