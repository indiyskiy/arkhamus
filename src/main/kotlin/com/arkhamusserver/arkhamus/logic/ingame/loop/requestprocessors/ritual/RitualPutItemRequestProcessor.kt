package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.ritual

import com.arkhamusserver.arkhamus.logic.ingame.GameEndLogic
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.RitualGoingDataHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.ritual.RitualPutItemRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.NettyRequestProcessor
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisAltarHolderRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisTimeEventRepository
import com.arkhamusserver.arkhamus.model.enums.GameEndReason
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.redis.RedisAltarHolder
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.apache.commons.lang3.math.NumberUtils.min
import org.springframework.stereotype.Component


@Component
class RitualPutItemRequestProcessor(
    private val inventoryHandler: InventoryHandler,
    private val redisAltarHolderRepository: RedisAltarHolderRepository,
    private val eventRepository: RedisTimeEventRepository,
    private val gameEndLogic: GameEndLogic,
    private val ritualGoingDataHandler: RitualGoingDataHandler
) : NettyRequestProcessor {

    override fun accept(request: NettyTickRequestMessageDataHolder): Boolean {
        return request.requestProcessData is RitualPutItemRequestProcessData
    }

    override fun process(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        val ritualPutItemRequestProcessData = requestDataHolder.requestProcessData as RitualPutItemRequestProcessData
        val item = ritualPutItemRequestProcessData.item
        val itemNumber = ritualPutItemRequestProcessData.itemNumber
        val altarHolder = globalGameData.altarHolder

        if (ritualPutItemRequestProcessData.canPut) {
            takeItemForRitual(
                item = item!!,
                itemNumber = itemNumber,
                altarHolder = altarHolder,
                user = ritualPutItemRequestProcessData.gameUser!!
            )
            ritualPutItemRequestProcessData.executedSuccessfully = true
            if (altarHolder.isAllItemsPut()) {
                gameEndLogic.endTheGame(globalGameData.game, GameEndReason.RITUAL_SUCCESS)
            } else {
                if (thisItemIsPutOnAltar(altarHolder, item)) {
                    shiftTimeOfEvent(ongoingEvents, item, altarHolder)
                }
            }
        }
    }


    private fun shiftTimeOfEvent(ongoingEvents: List<OngoingEvent>, item: Item, altarHolder: RedisAltarHolder) {
        ongoingEvents.filter {
            it.event.type == RedisTimeEventType.RITUAL_GOING &&
                    it.event.state == RedisTimeEventState.ACTIVE
        }.forEach { event ->
            val ritualEvent = event.event
            val notches = ritualGoingDataHandler.countItemsNotches(ritualEvent, altarHolder)
            val notchOfCurrentItem = notches.firstOrNull { it.itemId == item.id }
            if (notchOfCurrentItem != null) {
                val timeToAdd = notchOfCurrentItem.gameTimeEnd - (ritualEvent.timeStart + ritualEvent.timePast)
                if (timeToAdd > 0) {
                    ritualEvent.timePast += timeToAdd
                    ritualEvent.timeLeft -= timeToAdd
                    eventRepository.save(ritualEvent)
                }
            }
        }
    }

    private fun thisItemIsPutOnAltar(
        altarHolder: RedisAltarHolder,
        item: Item
    ) = (altarHolder.itemsOnAltars[item.id] ?: 0) >= (altarHolder.itemsForRitual[item.id] ?: 0)

    private fun takeItemForRitual(
        item: Item,
        itemNumber: Int,
        altarHolder: RedisAltarHolder,
        user: RedisGameUser
    ) {
        val itemsNeeded = (altarHolder.itemsForRitual[item.id] ?: 0) - (altarHolder.itemsOnAltars[item.id] ?: 0)
        val itemsInInventory = inventoryHandler.howManyItems(user, item)
        val itemsToTake = min(itemNumber, itemsNeeded, itemsInInventory)
        if (itemsToTake > 0) {
            val mutableMap = altarHolder.itemsOnAltars.toMutableMap()
            mutableMap[item.id] = (mutableMap[item.id] ?: 0) + itemsToTake
            altarHolder.itemsOnAltars = mutableMap
            redisAltarHolderRepository.save(altarHolder)

            user.items[item.id] = user.items[item.id]?.let { it - itemsToTake } ?: 0
        }
    }

    private fun RedisAltarHolder.isAllItemsPut(): Boolean =
        this.itemsForRitual.all { (itemId, number) ->
            this.itemsOnAltars[itemId] == number
        }

}

