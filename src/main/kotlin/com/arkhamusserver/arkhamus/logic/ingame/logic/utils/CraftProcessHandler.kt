package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.ingame.item.Recipe
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisCraftProcessRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.redis.RedisCraftProcess
import com.arkhamusserver.arkhamus.model.redis.RedisCrafter
import com.fasterxml.uuid.Generators
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class CraftProcessHandler(
    private val redisCraftProcessRepository: RedisCraftProcessRepository
) {

    @Transactional
    fun startCraftProcess(
        recipe: Recipe,
        crafter: RedisCrafter,
        sourceUserId: Long,
        gameId: Long,
        currentGameTime: Long
    ) {
        val craftProcessCast = RedisCraftProcess(
            id = Generators.timeBasedEpochGenerator().generate().toString(),
            gameId = gameId,
            recipeId = recipe.recipeId,
            sourceUserId = sourceUserId,
            targetCrafterId = crafter.inGameId(),
            timeStart = currentGameTime,
            timePast = 0,
            timeLeft = recipe.timeToCraft,
            state = RedisTimeEventState.ACTIVE,
        )
        redisCraftProcessRepository.save(craftProcessCast)
    }

}