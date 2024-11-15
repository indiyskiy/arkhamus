package com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.shortTime

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ShortTimeEventHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisShortTimeEvent
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.ShortTimeEventResponse
import org.springframework.stereotype.Component

@Component
class ShortTimeEventToResponseHandler(
    private val shortTimeEventHandler: ShortTimeEventHandler,
) {
    fun filterAndMap(
        events: List<RedisShortTimeEvent>,
        user: RedisGameUser,
        zones: List<LevelZone>,
        data: GlobalGameData
    ): List<ShortTimeEventResponse> {
        val filtered = shortTimeEventHandler.filter(events, user, zones, data)
        return mapFiltered(filtered)
    }

    private fun mapFiltered(
        events: List<RedisShortTimeEvent>,
    ): List<ShortTimeEventResponse> {
        return events.map {
            ShortTimeEventResponse(
                id = it.id,
                sourceType = it.type.getSource(),
                timeStart = it.timeStart,
                timePast = it.timePast,
                timeLeft = it.timeLeft,
                type = it.type,
                sourceId = it.sourceId,
                xLocation = it.xLocation,
                yLocation = it.yLocation,
            )
        }
    }
}