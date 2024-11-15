package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ritual

import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
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
            val currentItemId = countCurrentItem(gameTimeItemsNotches, currentGameTime)
            this.godId = altarHolder.lockedGodId
            this.altarsContent = mapAltarsContent(altarHolder)
            this.currentItemId = currentItemId
            this.currentItemMax = altarHolder.itemsForRitual[currentItemId] ?: 0
            this.currentItemInside = altarHolder.itemsOnAltars[currentItemId] ?: 0
            this.gameTimeStart = ritualEvent?.timeStart ?: 0
            this.gameTimeEnd = (ritualEvent?.timeStart ?: 0) + RedisTimeEventType.RITUAL_GOING.getDefaultTime()
            this.gameTimeNow = currentGameTime
            this.gameTimeItemsNotches = gameTimeItemsNotches
            this.userIdsInRitual = usersInRitual.map { it.userId }
        }
    }

    private fun countCurrentItem(gameTimeItemsNotches: List<ItemNotch>, currentGameTime: Long): Int {
        return gameTimeItemsNotches.firstOrNull {
            it.gameTimeStart <= currentGameTime &&
                    it.gameTimeEnd > currentGameTime
        }?.itemId ?: 0
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
            .map {
                it
            }.mapIndexed { index, (item, _) ->
                ItemNotch().apply {
                    itemId = item
                    gameTimeStart = start + (step * index)
                    gameTimeEnd = start + (step * (index + 1))
                }
            }
    }

    private fun mapAltarsContent(altarHolder: RedisAltarHolder?): List<AltarContent> {
        return altarHolder?.let { altar ->
            altar.itemsForRitual.map { (itemId, itemNumberMax) ->
                AltarContent().apply {
                    this.itemId = itemId
                    this.altarId = altar.itemsIdToAltarId[itemId]!!
                    this.itemNumberMax = itemNumberMax
                    this.itemNumberNow = altar.itemsOnAltars[itemId]!!
                }
            }
        } ?: emptyList()
    }
}