package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts

import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.RecipesSource
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisCraftProcessRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisCrafterRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.redis.RedisCraftProcess
import com.arkhamusserver.arkhamus.model.redis.RedisCrafter
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.InventoryCell
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.math.min

@Component
class OnTickCraftProcess(
    private val redisCraftProcessRepository: RedisCraftProcessRepository,
    private val redisCrafterRepository: RedisCrafterRepository,
    private val recipesSource: RecipesSource,
) {
    companion object {
        var logger: Logger = LoggerFactory.getLogger(OnTickCraftProcess::class.java)
    }

    @Transactional
    fun applyCraftProcess(
        globalGameData: GlobalGameData,
        castAbilities: List<RedisCraftProcess>,
        timePassedMillis: Long
    ) {
        castAbilities.forEach { craftProcess ->
            if (craftProcess.state == RedisTimeEventState.ACTIVE && craftProcess.timeLeft > 0) {
                val timeAdd = min(craftProcess.timeLeft, timePassedMillis)
                processActiveCraftProcess(craftProcess, timeAdd)
            } else {
                val recipe = recipesSource.byId(craftProcess.recipeId)!!
                val produced = recipe.item
                val numberOfItems = recipe.numberOfItems
                val crafter = globalGameData.crafters[craftProcess.targetCrafterId]!!
                logger.info("created $numberOfItems of ${produced.name}")
                addItemToCrafter(produced, numberOfItems, crafter)
                redisCraftProcessRepository.delete(craftProcess)
            }
        }
    }

    private fun addItemToCrafter(
        produced: Item,
        numberOfItems: Int,
        crafter: RedisCrafter
    ) {
        val existingCell = crafter.items.firstOrNull { it.item == produced }
        if (existingCell != null) {
            existingCell.number += numberOfItems
        } else {
            crafter.items += InventoryCell(produced, numberOfItems)
        }
        redisCrafterRepository.save(crafter)
    }

    private fun processActiveCraftProcess(
        craftProcess: RedisCraftProcess,
        timeAdd: Long,
    ) {
        if (craftProcess.timeLeft > 0) {
            craftProcess.timePast += timeAdd
            craftProcess.timeLeft -= timeAdd
        }
        if (craftProcess.timeLeft <= 0) {
            craftProcess.state = RedisTimeEventState.PAST
        }
        redisCraftProcessRepository.save(craftProcess)
    }
}