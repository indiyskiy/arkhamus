package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.timeevent

import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisTimeEventRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.redis.RedisTimeEvent
import com.fasterxml.uuid.Generators
import org.springframework.stereotype.Component

@Component
class NightTimeEventProcessor(
    private val timeEventRepository: RedisTimeEventRepository,
) : TimeEventProcessor {
    override fun accept(type: RedisTimeEventType): Boolean =
        type == RedisTimeEventType.NIGHT

    override fun processStart(event: RedisTimeEvent, currentGameTime: Long) {

    }

    override fun process(event: RedisTimeEvent, currentGameTime: Long) {

    }

    override fun processEnd(event: RedisTimeEvent, currentGameTime: Long) {
        event.state = RedisTimeEventState.PAST
        startTheDay(event, currentGameTime)
    }

    fun startTheDay(event: RedisTimeEvent, currentGameTime: Long) {
        createDay(event.gameId, currentGameTime)
    }

    fun startTheDay(game: GameSession, currentGameTime: Long) {
        createDay(game.id!!, currentGameTime)
    }

    private fun createDay(gameId: Long, currentGameTime: Long) {
        val night = RedisTimeEvent(
            id = Generators.timeBasedEpochGenerator().generate().toString(),
            gameId = gameId,
            sourceUserId = null,
            targetUserId = null,
            timeStart = currentGameTime,
            timeLeft = RedisTimeEventType.DAY.getDefaultTime(),
            timePast = 0L,
            type = RedisTimeEventType.DAY,
            state = RedisTimeEventState.ACTIVE
        )
        timeEventRepository.save(night)
    }

}