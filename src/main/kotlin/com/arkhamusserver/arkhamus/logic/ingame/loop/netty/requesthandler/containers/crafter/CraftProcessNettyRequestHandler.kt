package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.containers.crafter

import com.arkhamusserver.arkhamus.logic.ingame.item.RecipesSource
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.CanAbilityBeCastedHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.CanRecipeBeCraftedHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.CrafterProcessHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.EventVisibilityFilter
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.containers.crafter.CraftProcessRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.RequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.NettyRequestHandler
import com.arkhamusserver.arkhamus.view.dto.netty.request.containers.crafter.CraftProcessRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import org.springframework.stereotype.Component

@Component
class CraftProcessNettyRequestHandler(
    private val eventVisibilityFilter: EventVisibilityFilter,
    private val inventoryHandler: InventoryHandler,
    private val recipesSource: RecipesSource,
    private val canAbilityBeCastedHandler: CanAbilityBeCastedHandler,
    private val canRecipeBeCraftedHandler: CanRecipeBeCraftedHandler,
    private val crafterProcessHandler: CrafterProcessHandler
) : NettyRequestHandler {

    override fun acceptClass(nettyRequestMessage: NettyBaseRequestMessage): Boolean =
        nettyRequestMessage::class.java == CraftProcessRequestMessage::class.java

    override fun accept(nettyRequestMessage: NettyBaseRequestMessage): Boolean = true

    override fun buildData(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ): RequestProcessData {
        val userId = requestDataHolder.userAccount.id
        val request = requestDataHolder.nettyRequestMessage
        with(request as CraftProcessRequestMessage) {
            val recipe = recipesSource.byId(this.recipeId)
            val crafter = globalGameData.crafters[externalInventoryId]
            val user = globalGameData.users[userId]!!
            val users = globalGameData.users.values.filter { it.userId != userId }
            val sortedUserInventory = request.newInventoryContent

            return if (recipe != null && crafter != null) {
                val canBeCasted = canRecipeBeCraftedHandler.canUserCraft(user, recipe, crafter)
                CraftProcessRequestProcessData(
                    recipe = recipe,
                    crafter = crafter,
                    canBeStarted = canBeCasted,
                    sortedUserInventory = sortedUserInventory,
                    executedSuccessfully = false,
                    gameUser = user,
                    otherGameUsers = users,
                    visibleOngoingEvents = eventVisibilityFilter.filter(user, ongoingEvents),
                    availableAbilities = canAbilityBeCastedHandler.abilityOfUserResponses(user, globalGameData),
                    visibleItems = inventoryHandler.mapUsersItems(user.items),
                    tick = globalGameData.game.currentTick,
                    ongoingCraftingProcess = crafterProcessHandler.filterAndMap(
                        user,
                        globalGameData.crafters,
                        globalGameData.craftProcess
                    ),
                    containers = globalGameData.containers.values.toList(),
                )
            } else {
                CraftProcessRequestProcessData(
                    recipe = recipe,
                    crafter = crafter,
                    canBeStarted = false,
                    executedSuccessfully = false,
                    gameUser = user,
                    otherGameUsers = users,
                    sortedUserInventory = sortedUserInventory,
                    visibleOngoingEvents = eventVisibilityFilter.filter(user, ongoingEvents),
                    availableAbilities = canAbilityBeCastedHandler.abilityOfUserResponses(user, globalGameData),
                    visibleItems = inventoryHandler.mapUsersItems(user.items),
                    tick = globalGameData.game.currentTick,
                    ongoingCraftingProcess = crafterProcessHandler.filterAndMap(
                        user,
                        globalGameData.crafters,
                        globalGameData.craftProcess
                    ),
                    containers = globalGameData.containers.values.toList(),
                )
            }
        }
    }
}
