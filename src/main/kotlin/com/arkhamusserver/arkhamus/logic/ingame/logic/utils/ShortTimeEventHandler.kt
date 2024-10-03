package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.shortTime.SpecificShortTimeEventFilter
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisShortTimeEventRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.ShortTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisShortTimeEvent
import com.fasterxml.uuid.Generators
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ShortTimeEventHandler(
    private val specificShortTimeEventFilters: List<SpecificShortTimeEventFilter>,
    private val redisShortTimeEventRepository: RedisShortTimeEventRepository,
) {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(ShortTimeEventHandler::class.java)
    }

    fun filter(
        events: List<RedisShortTimeEvent>,
        user: RedisGameUser,
        zones: List<LevelZone>,
        data: GlobalGameData
    ): List<RedisShortTimeEvent> {
        return specificShortTimeEventFilters.map {
            it.filter(
                events,
                user,
                zones,
                data
            )
        }.flatten()
    }

    @Transactional
    fun createShortTimeEvent(
        userId: Long,
        gameId: Long,
        globalTimer: Long,
        type: ShortTimeEventType
    ) {
        redisShortTimeEventRepository.save(
            RedisShortTimeEvent(
                id = Generators.timeBasedEpochGenerator().generate().toString(),
                gameId = gameId,
                sourceId = userId,
                xLocation = null,
                yLocation = null,
                timeStart = globalTimer,
                timePast = 0,
                timeLeft = type.getTime(),
                type = type,
                state = RedisTimeEventState.ACTIVE,
            )
        )
    }

}