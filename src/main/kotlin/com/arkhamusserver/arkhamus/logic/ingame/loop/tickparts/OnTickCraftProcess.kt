package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts

import com.arkhamusserver.arkhamus.logic.ingame.item.RecipesSource
import com.arkhamusserver.arkhamus.logic.ingame.loop.ArkhamusOneTickLogic
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisCraftProcessRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisCrafterRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.redis.RedisCraftProcess
import com.arkhamusserver.arkhamus.model.redis.RedisCrafter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.math.min

@Component
class OnTickCraftProcess(
    private val redisCraftProcessRepository: RedisCraftProcessRepository,
    private val redisCrafterRepository: RedisCrafterRepository,
    private val recipesSource: RecipesSource
) {
    companion object {
        var logger: Logger = LoggerFactory.getLogger(OnTickCraftProcess::class.java)
    }

    @Transactional
    fun applyCraftProcess(
        globalGameData: GlobalGameData,
        castAbilities: List<RedisCraftProcess>,
        currentGameTime: Long
    ) {
        castAbilities.forEach { craftProcess ->
            if (craftProcess.state == RedisTimeEventState.ACTIVE && craftProcess.timeLeft > 0) {
                val timeAdd = min(craftProcess.timeLeft, ArkhamusOneTickLogic.TICK_DELTA)
                processActiveCraftProcess(craftProcess, timeAdd)
            } else {
                val recipe = recipesSource.byId(craftProcess.recipeId)!!
                val produced = recipe.item
                val numberOfItems = recipe.numberOfItems
                val crafter = globalGameData.crafters[craftProcess.targetCrafterId]!!
                logger.info("created $numberOfItems of ${produced.name}")
                addItemToCrafter(produced, numberOfItems, crafter)
                val newValueOfItemsInCrafter = crafter.items[produced.id]
                redisCraftProcessRepository.delete(craftProcess)
                logger.info("craft process ${craftProcess.id} finished, new value $newValueOfItemsInCrafter")
            }
        }
    }

    private fun addItemToCrafter(
        produced: Item,
        numberOfItems: Int,
        crafter: RedisCrafter
    ) {
        val before = crafter.items[produced.id] ?: 0
        crafter.items[produced.id] = before + numberOfItems
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