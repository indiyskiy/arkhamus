package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.timeevent

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserMadnessHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.TimeEventHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.InGameTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.InGameTimeEventState
import com.arkhamusserver.arkhamus.model.ingame.InGameTimeEvent
import org.springframework.stereotype.Component

@Component
class NightTimeEventProcessor(
    private val userLocationHandler: UserLocationHandler,
    private val userMadnessHandler: UserMadnessHandler,
    private val timeEventHandler: TimeEventHandler,
) : TimeEventProcessor {
    override fun accept(type: InGameTimeEventType): Boolean =
        type == InGameTimeEventType.NIGHT

    override fun processStart(
        event: InGameTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long,
        timePassedMillis: Long
    ) {

    }

    override fun process(
        event: InGameTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long,
        timePassedMillis: Long
    ) {
        globalGameData.users.filter {
            userLocationHandler.isInDarkness(it.value, globalGameData)
        }.forEach {
            userMadnessHandler.applyNightMadness(it.value, timePassedMillis, currentGameTime)
        }
    }

    override fun processEnd(
        event: InGameTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long,
        timePassedMillis: Long
    ) {
        event.state = InGameTimeEventState.PAST
        startTheDay(event, currentGameTime)
    }

    fun startTheDay(event: InGameTimeEvent, currentGameTime: Long) {
        createDay(event.gameId, currentGameTime)
    }

    fun startTheDay(game: GameSession, currentGameTime: Long) {
        createDay(game.id!!, currentGameTime)
    }

    private fun createDay(gameId: Long, currentGameTime: Long) {
        timeEventHandler.createEvent(
            gameId,
            InGameTimeEventType.DAY,
            currentGameTime,
            sourceObject = null
        )
    }

}