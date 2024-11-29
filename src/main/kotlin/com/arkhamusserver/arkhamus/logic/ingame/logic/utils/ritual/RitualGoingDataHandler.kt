package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ritual

import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.redis.RedisAltarHolder
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisTimeEvent
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.AltarContent
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.ItemNotch
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.RitualGoingDataResponse
import org.springframework.stereotype.Component

@Component
class RitualGoingDataHandler {

    fun build(
        ritualEvent: RedisTimeEvent?,
        altarHolder: RedisAltarHolder,
        usersInRitual: List<RedisGameUser>
    ): RitualGoingDataResponse {
        return RitualGoingDataResponse().apply {
            val currentGameTime = ritualEvent?.let { it.timeStart + it.timePast } ?: 0
            val gameTimeItemsNotches = countItemsNotches(ritualEvent, altarHolder)
            val currentItem = countCurrentItem(gameTimeItemsNotches, currentGameTime)
            this.godId = altarHolder.lockedGod?.getId()
            this.altarsContent = mapAltarsContent(altarHolder)
            this.currentItemId = currentItem?.id ?: 0
            this.currentItemMax = altarHolder.itemsForRitual[currentItem] ?: 0
            this.currentItemInside = altarHolder.itemsOnAltars[currentItem] ?: 0
            this.gameTimeStart = ritualEvent?.timeStart ?: 0
            this.gameTimeEnd = (ritualEvent?.timeStart ?: 0) + RedisTimeEventType.RITUAL_GOING.getDefaultTime()
            this.gameTimeNow = currentGameTime
            this.gameTimeItemsNotches = gameTimeItemsNotches
            this.userIdsInRitual = usersInRitual.map { it.userId }
        }
    }

    private fun countCurrentItem(gameTimeItemsNotches: List<ItemNotch>, currentGameTime: Long): Item? {
        return gameTimeItemsNotches.firstOrNull {
            it.gameTimeStart <= currentGameTime &&
                    it.gameTimeEnd > currentGameTime
        }?.item
    }

    fun countItemsNotches(
        ritualEvent: RedisTimeEvent?,
        altarHolder: RedisAltarHolder?,
    ): List<ItemNotch> {
        if (ritualEvent == null) return emptyList()
        if (altarHolder == null) return emptyList()
        val start = ritualEvent.timeStart
        val size = altarHolder.itemsForRitual.size
        val step = (ritualEvent.timePast + ritualEvent.timeLeft) / size
        return altarHolder.itemsForRitual
            .toList()
            .sortedBy { it.first }
            .mapIndexed { index, (item, _) ->
                ItemNotch().apply {
                    this.item = item
                    this.gameTimeStart = start + (step * index)
                    this.gameTimeEnd = start + (step * (index + 1))
                }
            }
    }

    private fun mapAltarsContent(altarHolder: RedisAltarHolder?): List<AltarContent> {
        return altarHolder?.let { altar ->
            altar.itemsForRitual.map { (item, itemNumberMax) ->
                AltarContent().apply {
                    this.item = item
                    this.altarId = altar.itemsToAltarId[item]!!
                    this.itemNumberMax = itemNumberMax
                    this.itemNumberNow = altar.itemsOnAltars[item]!!
                }
            }
        } ?: emptyList()
    }
}