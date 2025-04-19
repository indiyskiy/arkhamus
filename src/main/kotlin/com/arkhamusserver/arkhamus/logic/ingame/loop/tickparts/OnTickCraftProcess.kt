package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts

import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.RecipesSource
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameCraftProcessRepository
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameCrafterRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.InGameTimeEventState
import com.arkhamusserver.arkhamus.model.ingame.InGameCraftProcess
import com.arkhamusserver.arkhamus.model.ingame.InGameCrafter
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.InventoryCell
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.math.min

@Component
class OnTickCraftProcess(
    private val inGameCraftProcessRepository: InGameCraftProcessRepository,
    private val inGameCrafterRepository: InGameCrafterRepository,
    private val recipesSource: RecipesSource,
) {
    companion object {
        private val logger = LoggingUtils.getLogger<OnTickCraftProcess>()
    }

    @Transactional
    fun applyCraftProcess(
        globalGameData: GlobalGameData,
        castAbilities: List<InGameCraftProcess>,
        timePassedMillis: Long
    ) {
        castAbilities.forEach { craftProcess ->
            if (craftProcess.state == InGameTimeEventState.ACTIVE && craftProcess.timeLeft > 0) {
                val timeAdd = min(craftProcess.timeLeft, timePassedMillis)
                processActiveCraftProcess(craftProcess, timeAdd)
            } else {
                val recipe = recipesSource.byId(craftProcess.recipeId)!!
                val produced = recipe.item
                val numberOfItems = recipe.numberOfItems
                val crafter = globalGameData.crafters[craftProcess.targetCrafterId]!!
                logger.info("created $numberOfItems of ${produced.name}")
                addItemToCrafter(produced, numberOfItems, crafter)
                inGameCraftProcessRepository.delete(craftProcess)
            }
        }
    }

    private fun addItemToCrafter(
        produced: Item,
        numberOfItems: Int,
        crafter: InGameCrafter
    ) {
        val existingCell = crafter.items.firstOrNull { it.item == produced }
        if (existingCell != null) {
            existingCell.number += numberOfItems
        } else {
            crafter.items += InventoryCell(produced, numberOfItems)
        }
        inGameCrafterRepository.save(crafter)
    }

    private fun processActiveCraftProcess(
        craftProcess: InGameCraftProcess,
        timeAdd: Long,
    ) {
        if (craftProcess.timeLeft > 0) {
            craftProcess.timePast += timeAdd
            craftProcess.timeLeft -= timeAdd
        }
        if (craftProcess.timeLeft <= 0) {
            craftProcess.state = InGameTimeEventState.PAST
        }
        inGameCraftProcessRepository.save(craftProcess)
    }
}