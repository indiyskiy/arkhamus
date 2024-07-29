package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.RedisTimeEventHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.timeevent.NightTimeEventProcessor
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import org.springframework.stereotype.Component

@Component
class GameStartTimeEventLogic(
    private val nightTimeEventProcessor: NightTimeEventProcessor,
    private val redisTimeEventHandler: RedisTimeEventHandler,
) {
    fun createStartEvents(game: GameSession) {
        createFirstDay(game)
        createGodAwakenTimer(game)
    }

    private fun createGodAwakenTimer(game: GameSession) {
        redisTimeEventHandler.createDefaultEvent(
            game,
            RedisTimeEventType.GOD_AWAKEN,
            0
        )
    }

    private fun createFirstDay(game: GameSession) =
        nightTimeEventProcessor.startTheDay(game, 0L)

}
