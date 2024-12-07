package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.craft

import com.arkhamusserver.arkhamus.model.redis.RedisCraftProcess
import com.arkhamusserver.arkhamus.model.redis.RedisCrafter
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.CraftProcessResponse
import org.springframework.stereotype.Component

@Component
class CrafterProcessHandler {

    fun filterAndMap(
        user: RedisGameUser,
        crafters: Map<Long, RedisCrafter>,
        craftProcess: List<RedisCraftProcess>
    ): List<CraftProcessResponse> {
        val filteredCrafters = crafters.filter { it.value.holdingUser == user.inGameId() }
        val filteredCraftProcess = craftProcess.filterRelatedCrafters(user, filteredCrafters)
        return filteredCraftProcess.map {
            CraftProcessResponse(it)
        }
    }

    private fun List<RedisCraftProcess>.filterRelatedCrafters(
        user: RedisGameUser,
        filteredCrafters: Map<Long, RedisCrafter>
    ): List<RedisCraftProcess> {
        val mappedCrafters = filteredCrafters.values.map { it.inGameId() }.toSet()
        return this.filter {
            it.targetCrafterId in mappedCrafters || it.sourceUserId == user.inGameId()
        }
    }
}


