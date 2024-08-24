package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.TimeEventHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.enums.ingame.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.springframework.stereotype.Component

@Component
class SummonNightAbilityCast(
    private val timeEventHandler: TimeEventHandler
) : AbilityCast {
    override fun accept(ability: Ability): Boolean {
        return ability == Ability.SUMMON_NIGHT
    }

    override fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    ) {
        createSummonedNightEvent(
            globalGameData.game,
            abilityRequestProcessData.gameUser!!
        )
    }

    private fun createSummonedNightEvent(
        game: RedisGame,
        sourceUser: RedisGameUser
    ) {
        timeEventHandler.createEvent(
            game,
            RedisTimeEventType.SUMMONED_NIGHT,
            sourceUser
        )
    }
}