package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.timeevent

import com.arkhamusserver.arkhamus.logic.ingame.GameEndLogic
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.InGameTimeEventType
import com.arkhamusserver.arkhamus.model.ingame.InGameTimeEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class GameEndEventProcessor(
    private val gameEndLogic: GameEndLogic
) : TimeEventProcessor {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(GameEndLogic::class.java)
    }

    override fun accept(type: InGameTimeEventType): Boolean =
        type == InGameTimeEventType.GAME_END

    override fun processStart(
        event: InGameTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long,
        timePassedMillis: Long
    ) {
        logger.info("starting the end of game event")
    }

    @Transactional
    override fun process(
        event: InGameTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long,
        timePassedMillis: Long
    ) {
        if (globalGameData.users.values.all { it.techData.sawTheEndOfTimes }) {
            logger.info("all users saw the end")
            gameEndLogic.endTheGameCompletely(globalGameData.game)
        }
    }

    override fun processEnd(
        event: InGameTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long,
        timePassedMillis: Long
    ) {
        logger.info("end of game event ends")
        gameEndLogic.endTheGameCompletely(globalGameData.game)
    }

}