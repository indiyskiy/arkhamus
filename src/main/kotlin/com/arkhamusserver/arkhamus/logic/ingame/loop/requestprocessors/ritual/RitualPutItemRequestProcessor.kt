package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.ritual

import com.arkhamusserver.arkhamus.logic.ingame.GameEndLogic
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ritual.RitualGoingDataHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.TimeEventHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.ritual.RitualPutItemRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.NettyRequestProcessor
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisAltarHolderRepository
import com.arkhamusserver.arkhamus.model.enums.GameEndReason
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.redis.RedisAltarHolder
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.apache.commons.lang3.math.NumberUtils.min
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional


@Component
class RitualPutItemRequestProcessor(
    private val inventoryHandler: InventoryHandler,
    private val redisAltarHolderRepository: RedisAltarHolderRepository,
    private val eventHandler: TimeEventHandler,
    private val gameEndLogic: GameEndLogic,
    private val ritualGoingDataHandler: RitualGoingDataHandler
) : NettyRequestProcessor {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(RitualPutItemRequestProcessor::class.java)
    }

    override fun accept(request: NettyTickRequestMessageDataHolder): Boolean {
        return request.requestProcessData is RitualPutItemRequestProcessData
    }

    @Transactional
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
            logger.info("put item")
            takeItemForRitual(
                item = item!!,
                itemNumber = itemNumber,
                altarHolder = altarHolder,
                user = ritualPutItemRequestProcessData.gameUser!!
            )
            logger.info("put item executed")

            if (altarHolder?.isAllItemsPut() == true) {
                logger.info("put all item")
                if (globalGameData.game.god == altarHolder.lockedGod) {
                    gameEndLogic.endTheGame(
                        globalGameData.game,
                        globalGameData.users,
                        GameEndReason.RITUAL_SUCCESS
                    )
                } else {
                    justFinishRitual(ongoingEvents)
                }
            } else {
                logger.info("trying to shift time")
                if (thisItemIsPutOnAltar(altarHolder, item)) {
                    logger.info("shift time")
                    shiftTimeOfEvent(ongoingEvents, item, altarHolder)
                }
            }
            ritualPutItemRequestProcessData.executedSuccessfully = true
        }
    }

    private fun justFinishRitual(
        ongoingEvents: List<OngoingEvent>
    ) {
        findRitualEvent(ongoingEvents).forEach { event ->
            val ritualEvent = event.event
            val timeToAdd = ritualEvent.timeLeft - 1
            if (timeToAdd > 0) {
                eventHandler.pushEvent(ritualEvent, timeToAdd)
            }
        }
    }


    private fun shiftTimeOfEvent(
        ongoingEvents: List<OngoingEvent>,
        item: Item,
        altarHolder: RedisAltarHolder?
    ) {
        findRitualEvent(ongoingEvents).forEach { event ->
            pushEvent(event, altarHolder, item)
        }
    }

    private fun pushEvent(
        event: OngoingEvent,
        altarHolder: RedisAltarHolder?,
        item: Item
    ) {
        val ritualEvent = event.event
        val notches = ritualGoingDataHandler.countItemsNotches(ritualEvent, altarHolder)
        val notchOfCurrentItem = notches.firstOrNull { it.item == item }
        if (notchOfCurrentItem != null) {
            val timeToAdd = notchOfCurrentItem.gameTimeEnd - (ritualEvent.timeStart + ritualEvent.timePast)
            if (timeToAdd > 0) {
                eventHandler.pushEvent(ritualEvent, timeToAdd)
            }
        }
    }

    private fun findRitualEvent(ongoingEvents: List<OngoingEvent>) =
        ongoingEvents.filter {
            it.event.type == RedisTimeEventType.RITUAL_GOING &&
                    it.event.state == RedisTimeEventState.ACTIVE
        }


    private fun thisItemIsPutOnAltar(
        altarHolder: RedisAltarHolder?,
        item: Item
    ) = (altarHolder?.itemsOnAltars?.get(item) ?: 0) >= (altarHolder?.itemsForRitual?.get(item) ?: 0)

    private fun takeItemForRitual(
        item: Item,
        itemNumber: Int,
        altarHolder: RedisAltarHolder?,
        user: RedisGameUser
    ) {
        val itemsNeeded = (altarHolder?.itemsForRitual?.get(item) ?: 0) -
                (altarHolder?.itemsOnAltars?.get(item) ?: 0)
        val itemsInInventory = inventoryHandler.howManyItems(user, item)
        val itemsToTake = min(itemNumber, itemsNeeded, itemsInInventory)
        if (itemsToTake > 0) {
            val mutableMap = altarHolder?.itemsOnAltars?.toMutableMap()
            mutableMap?.set(item, (mutableMap[item] ?: 0) + itemsToTake)
            altarHolder?.itemsOnAltars = mutableMap ?: emptyMap()
            altarHolder?.let { redisAltarHolderRepository.save(it) }

            inventoryHandler.consumeItems(user, item, itemsToTake)
        }
    }

    private fun RedisAltarHolder.isAllItemsPut(): Boolean =
        this.itemsForRitual.all { (itemId, number) ->
            this.itemsOnAltars[itemId] == number
        }

}

