package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.ritual

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.ritual.RitualPutItemRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.NettyRequestProcessor
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisAltarHolderRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.model.redis.RedisAltarHolder
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.apache.commons.lang3.math.NumberUtils.min
import org.springframework.stereotype.Component


@Component
class RitualPutItemRequestProcessor(
    private val inventoryHandler: InventoryHandler,
    private val redisAltarHolderRepository: RedisAltarHolderRepository
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
        }
    }

    private fun takeItemForRitual(
        item: Item,
        itemNumber: Int,
        altarHolder: RedisAltarHolder,
        user: RedisGameUser
    ) {
        val itemsNeeded = (altarHolder.itemsForRitual[item.id] ?: 0) - (altarHolder.itemsOnAltars[item.id] ?: 0)
        val itemsInInventory = inventoryHandler.howManyItems(user, item)
        val itemsToTake = min(itemNumber.toLong(), itemsNeeded.toLong(), itemsInInventory)
        if (itemsToTake > 0) {
            altarHolder.itemsOnAltars = altarHolder.itemsOnAltars
            user.items[item.id] = user.items[item.id]?.let { it - itemsToTake } ?: 0
            redisAltarHolderRepository.save(altarHolder)
        }
    }

}