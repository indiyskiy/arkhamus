package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors

import com.arkhamusserver.arkhamus.logic.ingame.item.Recipe
import com.arkhamusserver.arkhamus.logic.ingame.logic.CraftProcessHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.CraftProcessRequestProcessData
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisCrafterRepository
import com.arkhamusserver.arkhamus.model.redis.RedisCrafter
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.InventoryCell
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.math.min

@Component
class CraftProcessRequestProcessor(
    private val craftProcessHandler: CraftProcessHandler,
    private val inventoryHandler: InventoryHandler,
    private val crafterRepository: RedisCrafterRepository
) : NettyRequestProcessor {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(CraftProcessRequestProcessor::class.java)
    }

    override fun accept(request: NettyTickRequestMessageDataHolder): Boolean {
        return request.requestProcessData is CraftProcessRequestProcessData
    }

    override fun process(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        val craftProcessRequestProcessData = requestDataHolder.requestProcessData as CraftProcessRequestProcessData
        craftProcessRequestProcessData.recipe?.let { recipe ->
            logger.info("started craft process for ${recipe.recipeId} for ${requestDataHolder.userAccount.nickName}")
            val canBeStarted = craftProcessRequestProcessData.canBeStarted
            logger.info("can be started = $canBeStarted")
            if (canBeStarted) {
                val crafter = craftProcessRequestProcessData.crafter!!
                val newSortedInventory = consumeItems(
                    recipe,
                    craftProcessRequestProcessData.gameUser!!,
                    crafter,
                    craftProcessRequestProcessData.sortedUserInventory
                )
                craftProcessRequestProcessData.sortedUserInventory = newSortedInventory
                craftProcessRequestProcessData.visibleItems = newSortedInventory
                createCraftProcess(
                    craftProcessRequestProcessData,
                    requestDataHolder.userAccount.id!!,
                    requestDataHolder.gameSession!!.id!!,
                    globalGameData.game.globalTimer
                )
                craftProcessRequestProcessData.executedSuccessfully = true
                crafterRepository.save(crafter)
            }
            logger.info("craft process for ${recipe.recipeId} end")
        } ?: {
            logger.warn("recipe is null for ${requestDataHolder.userAccount.nickName} of game ${requestDataHolder.gameSession!!.id}")
        }
    }

    private fun consumeItems(
        recipe: Recipe,
        gameUser: RedisGameUser,
        crafter: RedisCrafter,
        sortedUserInventory: List<InventoryCell>
    ): List<InventoryCell> {
        val consumedFromUser = inventoryHandler.consumeItems(recipe, gameUser, crafter)
        val updatedUserInventory = sortedUserInventory.toMutableList()
        return applyChangesOnSortedUsersInventory(updatedUserInventory, consumedFromUser)
    }

    private fun applyChangesOnSortedUsersInventory(
        updatedUserInventory: MutableList<InventoryCell>,
        consumedFromUser: List<InventoryHandler.ConsumedItem>
    ): MutableList<InventoryCell> {
        updatedUserInventory.forEach { cell ->
            val toConsume = consumedFromUser.firstOrNull { it.itemId == cell.itemId }
            if (toConsume != null) {
                val canBeConsumed = min(cell.number, toConsume.number)
                toConsume.number -= canBeConsumed
                cell.number -= canBeConsumed
            }
        }
        return updatedUserInventory
    }

    private fun createCraftProcess(
        craftProcessRequestProcessData: CraftProcessRequestProcessData,
        userId: Long,
        gameId: Long,
        globalTimer: Long
    ) {
        craftProcessHandler.startCraftProcess(
            craftProcessRequestProcessData.recipe!!,
            craftProcessRequestProcessData.crafter!!,
            userId,
            gameId,
            globalTimer
        )
    }
}