package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.ritual

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ability.CanAbilityBeCastHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.clues.ClueHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.craft.CrafterProcessHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest.QuestProgressHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ritual.RitualHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ZonesHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.EventVisibilityFilter
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.ritual.RitualPutItemRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.NettyRequestHandler
import com.arkhamusserver.arkhamus.model.enums.ingame.InGameTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.core.toItem
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.InGameTimeEventState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.UserStateTag
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.ritual.RitualPutItemRequestMessage
import org.springframework.stereotype.Component

@Component
class RitualPutItemNettyRequestHandler(
    private val eventVisibilityFilter: EventVisibilityFilter,
    private val canAbilityBeCastHandler: CanAbilityBeCastHandler,
    private val inventoryHandler: InventoryHandler,
    private val crafterProcessHandler: CrafterProcessHandler,
    private val zonesHandler: ZonesHandler,
    private val clueHandler: ClueHandler,
    private val questProgressHandler: QuestProgressHandler,
    private val ritualHandler: RitualHandler
) : NettyRequestHandler {

    override fun acceptClass(nettyRequestMessage: NettyBaseRequestMessage): Boolean =
        nettyRequestMessage::class.java == RitualPutItemRequestMessage::class.java

    override fun accept(nettyRequestMessage: NettyBaseRequestMessage): Boolean = true

    override fun buildData(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ): RitualPutItemRequestProcessData {
        val request = requestDataHolder.nettyRequestMessage
        with(request as RitualPutItemRequestMessage) {
            val inZones = zonesHandler.filterByPosition(
                requestDataHolder.nettyRequestMessage.baseRequestData.userPosition,
                globalGameData.levelGeometryData
            )
            val userId = requestDataHolder.userAccount.id
            val user = globalGameData.users[userId]!!
            val users = globalGameData.users.values.filter { it.inGameId() != userId }
            val altarHolder = globalGameData.altarHolder
            val item = this.itemId.toItem()
            val clues = clueHandler.filterClues(
                globalGameData.clues,
                user,
                globalGameData
            )
            val event = ongoingEvents.firstOrNull {
                it.event.type == InGameTimeEventType.RITUAL_GOING &&
                        it.event.state == InGameTimeEventState.ACTIVE
            }?.event
            val notches = ritualHandler.countItemsNotches(event, altarHolder)
            val currentNotch = ritualHandler.countCurrentNotch(notches, globalGameData.game.globalTimer)
            return RitualPutItemRequestProcessData(
                notches = notches,
                currentNotch = currentNotch,
                item = item,
                itemNumber = this.itemNumber,
                usersInRitual = users.filter { it.stateTags.contains(UserStateTag.IN_RITUAL) },
                currentGameTime = globalGameData.game.globalTimer,
                canPut = userCanPutItemOnAltar(this, user, item, globalGameData),
                ritualEvent = ongoingEvents.firstOrNull {
                    it.event.type == InGameTimeEventType.RITUAL_GOING &&
                            it.event.state == InGameTimeEventState.ACTIVE
                }?.event,
                altarHolder = altarHolder,
                executedSuccessfully = false,
                gameUser = user,
                otherGameUsers = users,
                inZones = inZones,
                visibleOngoingEvents = eventVisibilityFilter.filter(user, ongoingEvents),
                availableAbilities = canAbilityBeCastHandler.abilityOfUserResponses(user, globalGameData),
                visibleItems = inventoryHandler.mapUsersItems(user.items),
                ongoingCraftingProcess = crafterProcessHandler.filterAndMap(
                    user,
                    globalGameData.crafters,
                    globalGameData.craftProcess
                ),
                containers = globalGameData.containers.values.toList(),
                crafters = globalGameData.crafters.values.toList(),
                clues = clues,
                userQuestProgresses = questProgressHandler.mapQuestProgresses(
                    globalGameData,
                    globalGameData.questProgressByUserId,
                    user,
                    globalGameData.quests
                ),
                tick = globalGameData.game.currentTick
            )
        }
    }

    private fun userCanPutItemOnAltar(
        request: RitualPutItemRequestMessage,
        user: InGameUser,
        item: Item?,
        globalGameData: GlobalGameData
    ) = item != null &&
            request.itemNumber <= inventoryHandler.howManyItems(user, item) &&
            (globalGameData.altarHolder?.itemsForRitual?.containsKey(item) == true) &&
            ((globalGameData.altarHolder?.itemsForRitual?.get(item) ?: 0) >
                    (globalGameData.altarHolder?.itemsOnAltars?.get(item) ?: 0))

}
