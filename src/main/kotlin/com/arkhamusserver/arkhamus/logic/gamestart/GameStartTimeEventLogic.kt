package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.TimeEventHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.timeevent.NightTimeEventProcessor
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.InGameTimeEventType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class GameStartTimeEventLogic(
    private val nightTimeEventProcessor: NightTimeEventProcessor,
    private val timeEventHandler: TimeEventHandler,
) {

    @Transactional
    fun createStartEvents(game: GameSession) {
        createFirstDay(game)
        createGodAwakenTimer(game)
        createSummoningSickness(game)
    }

    private fun createGodAwakenTimer(game: GameSession) {
        timeEventHandler.createEvent(
            game,
            InGameTimeEventType.GOD_AWAKEN,
            0
        )
    }


    private fun createSummoningSickness(game: GameSession) {
        timeEventHandler.createEvent(
            game,
            InGameTimeEventType.SUMMONING_SICKNESS,
            0
        )
    }

    private fun createFirstDay(game: GameSession) =
        nightTimeEventProcessor.startTheDay(game, 0L)


}
