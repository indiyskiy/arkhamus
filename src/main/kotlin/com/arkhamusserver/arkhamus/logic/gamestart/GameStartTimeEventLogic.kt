package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.timeevent.NightTimeEventProcessor
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisTimeEventRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.redis.RedisTimeEvent
import com.fasterxml.uuid.Generators
import org.springframework.stereotype.Component

@Component
class GameStartTimeEventLogic(
    private val nightTimeEventProcessor: NightTimeEventProcessor,
    private val redisTimeEventRepository: RedisTimeEventRepository
) {
    fun createStartEvents(game: GameSession) {
        createFirstDay(game)
        createGodAwakenTimer(game)
    }

    private fun createGodAwakenTimer(game: GameSession) {
        val godAwakenTimer = RedisTimeEvent(
            id = Generators.timeBasedEpochGenerator().generate().toString(),
            gameId = game.id!!,
            timeStart = 0L,
            timePast = 0L,
            timeLeft = RedisTimeEventType.GOD_AWAKEN.getDefaultTime(),
            type = RedisTimeEventType.GOD_AWAKEN,
            state = RedisTimeEventState.ACTIVE
        )
        redisTimeEventRepository.save(godAwakenTimer)
    }

    private fun createFirstDay(game: GameSession) =
        nightTimeEventProcessor.startTheDay(game, 0L)

}
