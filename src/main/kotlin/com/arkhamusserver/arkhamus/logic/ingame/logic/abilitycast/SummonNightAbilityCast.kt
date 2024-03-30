package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisTimeEventRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisTimeEvent
import com.fasterxml.uuid.Generators
import org.springframework.stereotype.Component

@Component
class SummonNightAbilityCast(
    private val timeEventRepository: RedisTimeEventRepository,
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
            globalGameData.game.gameId!!,
            globalGameData.game.globalTimer,
            abilityRequestProcessData.gameUser!!
        )
    }

    private fun createSummonedNightEvent(
        gameId: Long,
        currentGameTime: Long,
        sourceUser: RedisGameUser
    ) {
        val night = RedisTimeEvent(
            id = Generators.timeBasedEpochGenerator().generate().toString(),
            gameId = gameId,
            sourceUserId = sourceUser.userId,
            targetUserId = null,
            timeStart = currentGameTime,
            timeLeft = RedisTimeEventType.NIGHT.getDefaultTime(),
            timePast = 0L,
            type = RedisTimeEventType.SUMMONED_NIGHT,
            state = RedisTimeEventState.ACTIVE,
            xLocation = sourceUser.x,
            yLocation = sourceUser.y
        )
        timeEventRepository.save(night)
    }
}