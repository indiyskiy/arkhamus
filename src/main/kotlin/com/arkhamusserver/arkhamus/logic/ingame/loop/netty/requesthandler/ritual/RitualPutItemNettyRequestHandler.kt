package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.ritual

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.CanAbilityBeCastedHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.CrafterProcessHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ZonesHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.EventVisibilityFilter
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.ritual.RitualPutItemRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.NettyRequestHandler
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.UserStateTag
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.ritual.RitualPutItemRequestMessage
import org.springframework.stereotype.Component

@Component
class RitualPutItemNettyRequestHandler(
    private val eventVisibilityFilter: EventVisibilityFilter,
    private val canAbilityBeCastedHandler: CanAbilityBeCastedHandler,
    private val inventoryHandler: InventoryHandler,
    private val crafterProcessHandler: CrafterProcessHandler,
    private val zonesHandler: ZonesHandler
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
            val inZones = zonesHandler.filterByUserPosition(
                requestDataHolder.nettyRequestMessage.baseRequestData.userPosition,
                globalGameData.levelGeometryData
            )
            val userId = requestDataHolder.userAccount.id
            val user = globalGameData.users[userId]!!
            val users = globalGameData.users.values.filter { it.userId != userId }
            val altarHolder = globalGameData.altarHolder
            val item = this.itemId.toItem()
            return RitualPutItemRequestProcessData(
                item = item,
                itemNumber = this.itemNumber,
                usersInRitual = users.filter { it.stateTags.contains(UserStateTag.IN_RITUAL.name) },
                currentGameTime = globalGameData.game.globalTimer,
                canPut = userCanPutItemOnAltar(this, user, item, globalGameData),
                ritualEvent = ongoingEvents.firstOrNull {
                    it.event.type == RedisTimeEventType.RITUAL_GOING &&
                            it.event.state == RedisTimeEventState.ACTIVE
                }?.event,
                altarHolder = altarHolder,
                executedSuccessfully = false,
                gameUser = user,
                otherGameUsers = users,
                inZones = inZones,
                visibleOngoingEvents = eventVisibilityFilter.filter(user, ongoingEvents),
                availableAbilities = canAbilityBeCastedHandler.abilityOfUserResponses(user, globalGameData),
                visibleItems = inventoryHandler.mapUsersItems(user.items),
                ongoingCraftingProcess = crafterProcessHandler.filterAndMap(
                    user,
                    globalGameData.crafters,
                    globalGameData.craftProcess
                ),
                containers = globalGameData.containers.values.toList(),
                tick = globalGameData.game.currentTick
            )
        }
    }

    private fun userCanPutItemOnAltar(
        request: RitualPutItemRequestMessage,
        user: RedisGameUser,
        item: Item?,
        globalGameData: GlobalGameData
    ) = item != null &&
            request.itemNumber <= inventoryHandler.howManyItems(user, item) &&
            globalGameData.altarHolder.itemsForRitual.containsKey(item.id) &&
            ((globalGameData.altarHolder.itemsForRitual[item.id] ?: 0) >
                    (globalGameData.altarHolder.itemsOnAltars[item.id] ?: 0))

    private fun Int?.toItem(): Item? = Item.values().firstOrNull { it.id == this }
}
