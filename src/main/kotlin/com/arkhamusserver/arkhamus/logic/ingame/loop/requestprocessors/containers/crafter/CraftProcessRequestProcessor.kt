package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.containers.crafter

import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.Recipe
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InventoryHandler.ConsumedItem
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.craft.CraftProcessHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ActivityHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.containers.crafter.CraftProcessRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.NettyRequestProcessor
import com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.containers.container.UpdateContainerRequestProcessor
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameCrafterRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.ActivityType
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.ingame.InGameCrafter
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.InventoryCell
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.math.min

@Component
class CraftProcessRequestProcessor(
    private val craftProcessHandler: CraftProcessHandler,
    private val inventoryHandler: InventoryHandler,
    private val crafterRepository: InGameCrafterRepository,
    private val activityHandler: ActivityHandler
) : NettyRequestProcessor {

    companion object {
        private val logger = LoggingUtils.getLogger<CraftProcessRequestProcessor>()
    }

    override fun accept(request: NettyTickRequestMessageDataHolder): Boolean {
        return request.requestProcessData is CraftProcessRequestProcessData
    }

    @Transactional
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
                craft(craftProcessRequestProcessData, recipe, requestDataHolder, globalGameData)
                logger.info("craft process for ${recipe.recipeId} started")
                activityHandler.addUserWithTargetActivity(
                    globalGameData.game.inGameId(),
                    ActivityType.CRAFT_STARTED,
                    craftProcessRequestProcessData.gameUser!!,
                    globalGameData.game.globalTimer,
                    GameObjectType.CRAFTER,
                    craftProcessRequestProcessData.crafter,
                    recipe.recipeId.toLong()
                )
            }
        } ?: {
            logger.warn("recipe is null for ${requestDataHolder.userAccount.nickName} of game ${requestDataHolder.gameSession!!.id}")
        }
    }

    private fun craft(
        craftProcessRequestProcessData: CraftProcessRequestProcessData,
        recipe: Recipe,
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData
    ) {
        val crafter = craftProcessRequestProcessData.crafter!!
        val user = craftProcessRequestProcessData.gameUser!!
        val inventory = craftProcessRequestProcessData.sortedUserInventory
        val newSortedInventory = consumeItems(
            recipe,
            user,
            crafter,
            inventory
        )
        createCraftProcess(
            craftProcessRequestProcessData,
            requestDataHolder.userAccount.id!!,
            requestDataHolder.gameSession!!.id!!,
            globalGameData.game.globalTimer
        )
        crafterRepository.save(crafter)
        craftProcessRequestProcessData.sortedUserInventory = newSortedInventory
        craftProcessRequestProcessData.visibleItems = newSortedInventory
        craftProcessRequestProcessData.executedSuccessfully = true

    }

    private fun consumeItems(
        recipe: Recipe,
        gameUser: InGameUser,
        crafter: InGameCrafter,
        sortedUserInventory: List<InventoryCell>
    ): List<InventoryCell> {
        val consumedFromUser = inventoryHandler.consumeItems(recipe, gameUser, crafter)
        val updatedUserInventory = sortedUserInventory.toMutableList()
        return applyChangesOnSortedUsersInventory(updatedUserInventory, consumedFromUser)
    }

    private fun applyChangesOnSortedUsersInventory(
        updatedUserInventory: MutableList<InventoryCell>,
        consumedFromUser: List<ConsumedItem>
    ): MutableList<InventoryCell> {
        updatedUserInventory.forEach { cell ->
            val toConsume = consumedFromUser.firstOrNull { it.item == cell.item }
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