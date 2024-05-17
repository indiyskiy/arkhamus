package com.arkhamusserver.arkhamus.logic.ingame.logic

import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.AbilityCast
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.AbilityRequestProcessData
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisAbilityCastRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.redis.RedisAbilityCast
import com.fasterxml.uuid.Generators
import org.springframework.stereotype.Component

@Component
class AbilityCastHandler(
    private val abilityCasts: List<AbilityCast>,
    private val redisAbilityCastRepository: RedisAbilityCastRepository
) {
    fun cast(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        globalGameData: GlobalGameData
    ) {
        abilityCasts
            .first { it.accept(ability) }
            .cast(ability, abilityRequestProcessData, globalGameData)
    }

    fun createCastAbilityEvent(
        ability: Ability,
        abilityRequestProcessData: AbilityRequestProcessData,
        sourceUserId: Long,
        gameId: Long,
        currentGameTime: Long
    ) {
        val abilityCast = RedisAbilityCast(
            id = Generators.timeBasedEpochGenerator().generate().toString(),
            gameId = gameId,
            abilityId = ability.id,
            sourceUserId = sourceUserId,
            targetUserId = null,
            timeStart = currentGameTime,
            timePast = 0,
            timeLeft = ability.cooldown ?: 0L,
            state = if (ability.cooldown != null) RedisTimeEventState.ACTIVE else RedisTimeEventState.PAST,
            xLocation = null,
            yLocation = null,
        )
        redisAbilityCastRepository.save(abilityCast)
    }

}