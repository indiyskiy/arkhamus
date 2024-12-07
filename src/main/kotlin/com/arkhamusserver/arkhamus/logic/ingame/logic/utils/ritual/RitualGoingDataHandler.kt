package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ritual

import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.redis.RedisAltarHolder
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisTimeEvent
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.*
import org.springframework.stereotype.Component

@Component
class RitualGoingDataHandler {
    fun build(
        ritualEvent: RedisTimeEvent?,
        altarHolder: RedisAltarHolder,
        usersInRitual: List<RedisGameUser>,
        currentItem: Item?,
        gameTimeItemsNotches: List<ItemNotch>
    ): RitualGoingDataResponse {
        return RitualGoingDataResponse().apply {
            val currentGameTime = ritualEvent?.let { it.timeStart + it.timePast } ?: 0
            this.godId = altarHolder.lockedGod?.getId()
            this.altarsContent = mapAltarsContent(altarHolder).map { AltarContentResponse(it) }
            this.currentItemId = currentItem?.id ?: 0
            this.currentItemMax = altarHolder.itemsForRitual[currentItem] ?: 0
            this.currentItemInside = altarHolder.itemsOnAltars[currentItem] ?: 0
            this.gameTimeStart = ritualEvent?.timeStart ?: 0
            this.gameTimeEnd = (ritualEvent?.timeStart ?: 0) + RedisTimeEventType.RITUAL_GOING.getDefaultTime()
            this.gameTimeNow = currentGameTime
            this.gameTimeItemsNotches = gameTimeItemsNotches.map {
                ItemNotchResponse(it)
            }
            this.userIdsInRitual = usersInRitual.map { it.inGameId() }
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

    fun buildUserData(
        holder: RedisAltarHolder,
        myUser: RedisGameUser
    ): UserRitualData {
        val inRitual = holder.usersInRitual.contains(myUser.inGameId())
        val canLeave = inRitual && !holder.usersToKick.contains(myUser.inGameId())
        val leftAlready = inRitual && !canLeave
        val usersInRitual = holder.usersInRitual.size
        val usersGoingToLeave = holder.usersToKick.size
        val delimeter = if (leftAlready) {
            usersGoingToLeave
        } else {
            usersGoingToLeave + 1
        }
        val madnessPenalty = if (usersInRitual >= usersGoingToLeave - 1) {
            RitualHandler.MADNESS_PER_USER * usersInRitual / delimeter
        } else {
            0.0
        }

        return UserRitualData(
            inRitual = inRitual,
            canLeave = canLeave,
            leftAlready = leftAlready,
            usersInRitual = usersInRitual,
            usersGoingToLeave = usersGoingToLeave,
            madnessPenalty = madnessPenalty,
        )
    }
}