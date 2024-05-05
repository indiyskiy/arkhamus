package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler

import com.arkhamusserver.arkhamus.logic.ingame.item.RecipesSource
import com.arkhamusserver.arkhamus.logic.ingame.logic.CanAbilityBeCastedHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.CanRecipeBeCraftedHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.EventVisibilityFilter
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.CraftProcessRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.RequestProcessData
import com.arkhamusserver.arkhamus.view.dto.netty.request.CraftProcessRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import org.springframework.stereotype.Component

@Component
class CraftProcessNettyRequestHandler(
    private val eventVisibilityFilter: EventVisibilityFilter,
    private val inventoryHandler: InventoryHandler,
    private val recipesSource: RecipesSource,
    private val canAbilityBeCastedHandler: CanAbilityBeCastedHandler,
    private val canRecipeBeCraftedHandler: CanRecipeBeCraftedHandler,
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
            val crafter = globalGameData.crafters[crafterId]
            val user = globalGameData.users[userId]!!
            val users = globalGameData.users.values.filter { it.userId != userId }

            return if (recipe != null && crafter != null) {
                val canBeCasted = canRecipeBeCraftedHandler.canUserCraft(user, recipe, crafter)
                CraftProcessRequestProcessData(
                    recipe = recipe,
                    crafter = crafter,
                    canBeStarted = canBeCasted,
                    startedSuccessfully = false,
                    gameUser = user,
                    otherGameUsers = users,
                    visibleOngoingEvents = eventVisibilityFilter.filter(user, ongoingEvents),
                    availableAbilities = canAbilityBeCastedHandler.abilityOfUserResponses(user, globalGameData),
                    visibleItems = inventoryHandler.mapUsersItems(user.items),
                    tick = globalGameData.game.currentTick
                )
            } else {
                CraftProcessRequestProcessData(
                    recipe = recipe,
                    crafter = crafter,
                    canBeStarted = false,
                    startedSuccessfully = false,
                    gameUser = user,
                    otherGameUsers = users,
                    visibleOngoingEvents = eventVisibilityFilter.filter(user, ongoingEvents),
                    availableAbilities = canAbilityBeCastedHandler.abilityOfUserResponses(user, globalGameData),
                    visibleItems = inventoryHandler.mapUsersItems(user.items),
                    tick = globalGameData.game.currentTick
                )
            }
        }
    }
}
