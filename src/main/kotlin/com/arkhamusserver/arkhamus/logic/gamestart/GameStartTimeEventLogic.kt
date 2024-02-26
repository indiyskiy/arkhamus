package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.timeevent.NightTimeEventProcessor
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import org.springframework.stereotype.Component

@Component
class GameStartTimeEventLogic(
    private val nightTimeEventProcessor: NightTimeEventProcessor
) {
    fun createStartEvents(game: GameSession) {
        createFirstDay(game)
    }

    private fun createFirstDay(game: GameSession) =
        nightTimeEventProcessor.startTheDay(game, 0L)

}
