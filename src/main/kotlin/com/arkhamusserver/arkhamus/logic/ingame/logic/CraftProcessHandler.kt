package com.arkhamusserver.arkhamus.logic.ingame.logic

import com.arkhamusserver.arkhamus.logic.ingame.item.Recipe
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisCraftProcessRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.redis.RedisCraftProcess
import com.arkhamusserver.arkhamus.model.redis.RedisCrafter
import com.fasterxml.uuid.Generators
import org.springframework.stereotype.Component

@Component
class CraftProcessHandler(
    private val redisCraftProcessRepository: RedisCraftProcessRepository
) {
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
            targetCrafterId = crafter.crafterId,
            timeStart = currentGameTime,
            timePast = 0,
            timeLeft = recipe.timeToCraft,
            state = RedisTimeEventState.ACTIVE,
        )
        redisCraftProcessRepository.save(craftProcessCast)
    }

}