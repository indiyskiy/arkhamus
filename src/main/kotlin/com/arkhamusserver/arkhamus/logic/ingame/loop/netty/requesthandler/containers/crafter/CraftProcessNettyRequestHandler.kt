package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.containers.crafter

import com.arkhamusserver.arkhamus.logic.ingame.item.RecipesSource
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.*
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest.QuestProgressHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.EventVisibilityFilter
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.RequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.containers.crafter.CraftProcessRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.NettyRequestHandler
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.containers.crafter.CraftProcessRequestMessage
import org.springframework.stereotype.Component

@Component
class CraftProcessNettyRequestHandler(
    private val eventVisibilityFilter: EventVisibilityFilter,
    private val inventoryHandler: InventoryHandler,
    private val recipesSource: RecipesSource,
    private val canAbilityBeCastHandler: CanAbilityBeCastHandler,
    private val canRecipeBeCraftedHandler: CanRecipeBeCraftedHandler,
    private val crafterProcessHandler: CrafterProcessHandler,
    private val zonesHandler: ZonesHandler,
    private val clueHandler: ClueHandler,
    private val questProgressHandler: QuestProgressHandler,
) : NettyRequestHandler {

    override fun acceptClass(nettyRequestMessage: NettyBaseRequestMessage): Boolean =
        nettyRequestMessage::class.java == CraftProcessRequestMessage::class.java

    override fun accept(nettyRequestMessage: NettyBaseRequestMessage): Boolean = true

    override fun buildData(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ): RequestProcessData {
        val inZones = zonesHandler.filterByPosition(
            requestDataHolder.nettyRequestMessage.baseRequestData.userPosition,
            globalGameData.levelGeometryData
        )
        val userId = requestDataHolder.userAccount.id
        val request = requestDataHolder.nettyRequestMessage
        with(request as CraftProcessRequestMessage) {
            val recipe = recipesSource.byId(this.recipeId)
            val crafter = globalGameData.crafters[externalInventoryId]
            val user = globalGameData.users[userId]!!
            val users = globalGameData.users.values.filter { it.userId != userId }
            val clues = clueHandler.filterClues(
                globalGameData.clues,
                inZones,
                globalGameData.castAbilities,
                userId!!
            )
            val sortedUserInventory = request.newInventoryContent

            return if (recipe != null && crafter != null) {
                val canBeCrafted = canRecipeBeCraftedHandler.canUserCraft(user, recipe, crafter)
                CraftProcessRequestProcessData(
                    recipe = recipe,
                    crafter = crafter,
                    canBeStarted = canBeCrafted,
                    sortedUserInventory = sortedUserInventory,
                    executedSuccessfully = false,
                    gameUser = user,
                    otherGameUsers = users,
                    inZones = inZones,
                    visibleOngoingEvents = eventVisibilityFilter.filter(user, ongoingEvents),
                    availableAbilities = canAbilityBeCastHandler.abilityOfUserResponses(user, globalGameData),
                    visibleItems = inventoryHandler.mapUsersItems(user.items),
                    tick = globalGameData.game.currentTick,
                    ongoingCraftingProcess = crafterProcessHandler.filterAndMap(
                        user,
                        globalGameData.crafters,
                        globalGameData.craftProcess
                    ),
                    containers = globalGameData.containers.values.toList(),
                    crafters = globalGameData.crafters.values.toList(),
                    clues = clues,
                    userQuestProgresses = questProgressHandler.filterQuestProgresses(
                        globalGameData.questProgressByUserId,
                        user
                    ),
                )
            } else {
                CraftProcessRequestProcessData(
                    recipe = recipe,
                    crafter = crafter,
                    canBeStarted = false,
                    executedSuccessfully = false,
                    gameUser = user,
                    otherGameUsers = users,
                    inZones = inZones,
                    sortedUserInventory = sortedUserInventory,
                    visibleOngoingEvents = eventVisibilityFilter.filter(user, ongoingEvents),
                    availableAbilities = canAbilityBeCastHandler.abilityOfUserResponses(user, globalGameData),
                    visibleItems = inventoryHandler.mapUsersItems(user.items),
                    tick = globalGameData.game.currentTick,
                    ongoingCraftingProcess = crafterProcessHandler.filterAndMap(
                        user,
                        globalGameData.crafters,
                        globalGameData.craftProcess
                    ),
                    containers = globalGameData.containers.values.toList(),
                    crafters = globalGameData.crafters.values.toList(),
                    clues = clues,
                    userQuestProgresses = questProgressHandler.filterQuestProgresses(
                        globalGameData.questProgressByUserId,
                        user
                    ),
                )
            }
        }
    }
}
