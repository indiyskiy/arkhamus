package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.TimeEventHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithStringId
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
    ): Boolean {
        createSummonedNightEvent(
            globalGameData.game,
            abilityRequestProcessData.gameUser!!
        )
        return true
    }

    override fun cast(
        sourceUser: RedisGameUser,
        ability: Ability,
        target: WithStringId?,
        globalGameData: GlobalGameData
    ): Boolean {
        createSummonedNightEvent(
            globalGameData.game,
            sourceUser
        )
        return true
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