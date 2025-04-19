package com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.shortTime

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ShortTimeEventHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.model.ingame.InGameShortTimeEvent
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.ShortTimeEventResponse
import org.springframework.stereotype.Component

@Component
class ShortTimeEventToResponseHandler(
    private val shortTimeEventHandler: ShortTimeEventHandler,
) {
    fun filterAndMap(
        events: List<InGameShortTimeEvent>,
        user: InGameUser,
        zones: List<LevelZone>,
        data: GlobalGameData
    ): List<ShortTimeEventResponse> {
        val filtered = shortTimeEventHandler.filter(events, user, zones, data)
        return mapFiltered(filtered)
    }

    private fun mapFiltered(
        events: List<InGameShortTimeEvent>,
    ): List<ShortTimeEventResponse> {
        return events.map {
            ShortTimeEventResponse(
                id = it.id,
                sourceType = it.type.getSource(),
                timeStart = it.timeStart,
                timePast = it.timePast,
                timeLeft = it.timeLeft,
                type = it.type,
                sourceId = it.objectId,
                xLocation = it.xLocation,
                yLocation = it.yLocation,
                additionalData = it.additionalData
            )
        }
    }
}