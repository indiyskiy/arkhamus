package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ritual

import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.redis.RedisAltarHolder
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisTimeEvent
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.AltarContent
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.AltarContentResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.ItemNotchResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.RitualGoingDataResponse
import org.springframework.stereotype.Component

@Component
class RitualGoingDataHandler(
    private val ritualHandler: RitualHandler
) {
    fun build(
        ritualEvent: RedisTimeEvent?,
        altarHolder: RedisAltarHolder,
        usersInRitual: List<RedisGameUser>
    ): RitualGoingDataResponse {
        return RitualGoingDataResponse().apply {
            val currentGameTime = ritualEvent?.let { it.timeStart + it.timePast } ?: 0
            val gameTimeItemsNotches = ritualHandler.countItemsNotches(ritualEvent, altarHolder)
            val currentItem = ritualHandler.countCurrentItem(gameTimeItemsNotches, currentGameTime)
            this.godId = altarHolder.lockedGod?.getId()
            this.altarsContent = mapAltarsContent(altarHolder).map { AltarContentResponse(it) }
            this.currentItemId = currentItem?.id ?: 0
            this.currentItemMax = altarHolder.itemsForRitual[currentItem] ?: 0
            this.currentItemInside = altarHolder.itemsOnAltars[currentItem] ?: 0
            this.gameTimeStart = ritualEvent?.timeStart ?: 0
            this.gameTimeEnd = (ritualEvent?.timeStart ?: 0) + RedisTimeEventType.RITUAL_GOING.getDefaultTime()
            this.gameTimeNow = currentGameTime
            this.gameTimeItemsNotches = gameTimeItemsNotches.map{
                ItemNotchResponse(it)
            }
            this.userIdsInRitual = usersInRitual.map { it.userId }
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