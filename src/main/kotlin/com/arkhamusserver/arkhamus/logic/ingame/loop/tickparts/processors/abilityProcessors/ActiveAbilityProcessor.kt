package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.abilityProcessors

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.redis.RedisAbilityCast

interface ActiveAbilityProcessor {
    fun accepts(castAbility: RedisAbilityCast): Boolean
    fun processActive(castAbility: RedisAbilityCast, globalGameData: GlobalGameData)
    fun finishActive(castAbility: RedisAbilityCast, globalGameData: GlobalGameData)
}