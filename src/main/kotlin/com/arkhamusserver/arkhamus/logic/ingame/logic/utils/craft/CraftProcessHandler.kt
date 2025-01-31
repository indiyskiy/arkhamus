package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.craft

import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.Recipe
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.generateRandomId
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameCraftProcessRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.InGameTimeEventState
import com.arkhamusserver.arkhamus.model.ingame.InGameCraftProcess
import com.arkhamusserver.arkhamus.model.ingame.InGameCrafter
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class CraftProcessHandler(
    private val inGameCraftProcessRepository: InGameCraftProcessRepository
) {

    @Transactional
    fun startCraftProcess(
        recipe: Recipe,
        crafter: InGameCrafter,
        sourceUserId: Long,
        gameId: Long,
        currentGameTime: Long
    ) {
        val craftProcessCast = InGameCraftProcess(
            id = generateRandomId(),
            gameId = gameId,
            recipeId = recipe.recipeId,
            sourceUserId = sourceUserId,
            targetCrafterId = crafter.inGameId(),
            timeStart = currentGameTime,
            timePast = 0,
            timeLeft = recipe.timeToCraft,
            state = InGameTimeEventState.ACTIVE,
        )
        inGameCraftProcessRepository.save(craftProcessCast)
    }

}