package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.timeevent

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.TimeEventHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserMadnessHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.redis.RedisTimeEvent
import org.springframework.stereotype.Component

@Component
class NightTimeEventProcessor(
    private val userLocationHandler: UserLocationHandler,
    private val userMadnessHandler: UserMadnessHandler,
    private val timeEventHandler: TimeEventHandler,
) : TimeEventProcessor {
    override fun accept(type: RedisTimeEventType): Boolean =
        type == RedisTimeEventType.NIGHT

    override fun processStart(
        event: RedisTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long
    ) {

    }

    override fun process(
        event: RedisTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long
    ) {
        globalGameData.users.filter {
            userLocationHandler.isInDarkness(it.value, globalGameData)
        }.forEach {
            userMadnessHandler.applyNightMadness(it.value)
        }
    }

    override fun processEnd(
        event: RedisTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long
    ) {
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
        timeEventHandler.createEvent(
            gameId,
            RedisTimeEventType.DAY,
            currentGameTime,
            sourceObject = null
        )
    }

}