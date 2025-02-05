package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ritual

import com.arkhamusserver.arkhamus.model.enums.ingame.InGameTimeEventType
import com.arkhamusserver.arkhamus.model.ingame.InGameAltarHolder
import com.arkhamusserver.arkhamus.model.ingame.InGameTimeEvent
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.*
import org.springframework.stereotype.Component

@Component
class RitualMappingDataHandler {
    fun build(
        ritualEvent: InGameTimeEvent?,
        altarHolder: InGameAltarHolder,
        usersInRitual: List<InGameUser>,
        currentNotch: ItemNotch?,
        gameTimeItemsNotches: List<ItemNotch>
    ): RitualGoingDataResponse {
        return RitualGoingDataResponse().apply {
            val altarsContent = mapAltarsContent(altarHolder).map { AltarContentResponse(it) }
            val currentGameTime = ritualEvent?.let { it.timeStart + it.timePast } ?: 0
            this.godId = altarHolder.lockedGod?.getId()
            this.altarsContent = altarsContent
            this.currentItemId = currentNotch?.item?.id ?: 0
            this.currentAltarId = currentNotch?.altarId ?: 0
            this.currentItemMax = altarHolder.itemsForRitual[currentNotch?.item] ?: 0
            this.currentItemInside = altarHolder.itemsOnAltars[currentNotch?.item] ?: 0
            this.gameTimeStart = ritualEvent?.timeStart ?: 0
            this.gameTimeEnd = (ritualEvent?.timeStart ?: 0) + InGameTimeEventType.RITUAL_GOING.getDefaultTime()
            this.gameTimeNow = currentGameTime
            this.gameTimeItemsNotches = gameTimeItemsNotches.map {
                ItemNotchResponse( it)
            }
            this.userIdsInRitual = usersInRitual.map { it.inGameId() }
        }
    }

    private fun mapAltarsContent(altarHolder: InGameAltarHolder?): List<AltarContent> {
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
        holder: InGameAltarHolder,
        myUser: InGameUser
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
        val madnessPenalty = if (delimeter != 0 && usersInRitual >= usersGoingToLeave - 1) {
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