package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.craft

import com.arkhamusserver.arkhamus.model.ingame.InGameCraftProcess
import com.arkhamusserver.arkhamus.model.ingame.InGameCrafter
import com.arkhamusserver.arkhamus.model.ingame.InGameGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.CraftProcessResponse
import org.springframework.stereotype.Component

@Component
class CrafterProcessHandler {

    fun filterAndMap(
        user: InGameGameUser,
        crafters: Map<Long, InGameCrafter>,
        craftProcess: List<InGameCraftProcess>
    ): List<CraftProcessResponse> {
        val filteredCrafters = crafters.filter { it.value.holdingUser == user.inGameId() }
        val filteredCraftProcess = craftProcess.filterRelatedCrafters(user, filteredCrafters)
        return filteredCraftProcess.map {
            CraftProcessResponse(it)
        }
    }

    private fun List<InGameCraftProcess>.filterRelatedCrafters(
        user: InGameGameUser,
        filteredCrafters: Map<Long, InGameCrafter>
    ): List<InGameCraftProcess> {
        val mappedCrafters = filteredCrafters.values.map { it.inGameId() }.toSet()
        return this.filter {
            it.targetCrafterId in mappedCrafters || it.sourceUserId == user.inGameId()
        }
    }
}


