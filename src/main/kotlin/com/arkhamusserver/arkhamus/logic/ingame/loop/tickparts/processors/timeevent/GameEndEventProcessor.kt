package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.timeevent

import com.arkhamusserver.arkhamus.logic.ingame.GameEndLogic
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.redis.RedisTimeEvent
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

    override fun accept(type: RedisTimeEventType): Boolean =
        type == RedisTimeEventType.GAME_END

    override fun processStart(
        event: RedisTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long
    ) {
        logger.info("starting the end of game event")
    }

    @Transactional
    override fun process(
        event: RedisTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long
    ) {
        if (globalGameData.users.values.all { it.sawTheEndOfTimes }) {
            logger.info("all users saw the end")
            gameEndLogic.endTheGameCompletely(globalGameData.game)
        }
    }

    override fun processEnd(
        event: RedisTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long
    ) {
        logger.info("end of game event ends")
        gameEndLogic.endTheGameCompletely(globalGameData.game)
    }

}