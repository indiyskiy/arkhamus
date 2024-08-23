package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisAbilityCastRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.redis.RedisAbilityCast
import com.fasterxml.uuid.Generators
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class CreateCastAbilityEventHandler(
    private val redisAbilityCastRepository: RedisAbilityCastRepository,
) {

    @Transactional
    fun createCastAbilityEvent(
        ability: Ability,
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
            timeLeftActive = ability.active ?: 0L,
            timeLeftCooldown = setCooldown(ability),
            state = setState(ability),
            xLocation = null,
            yLocation = null,
        )
        redisAbilityCastRepository.save(abilityCast)
    }

    private fun setCooldown(ability: Ability) =
        if (ability.cooldown >= (ability.active ?: 0L)) {
            ability.cooldown
        } else {
            ability.active ?: 0L
        }

    private fun setState(ability: Ability) =
        if ((ability.active ?: 0L) > 0) {
            RedisTimeEventState.ACTIVE
        } else {
            RedisTimeEventState.ON_COOLDOWN
        }

}