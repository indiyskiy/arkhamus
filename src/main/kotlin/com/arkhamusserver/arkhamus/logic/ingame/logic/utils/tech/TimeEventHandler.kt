package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech

import com.arkhamusserver.arkhamus.logic.ingame.logic.Location
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameTimeEventRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.InGameTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.InGameTimeEventState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.ingame.InRamGame
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.InGameTimeEvent
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithTrueIngameId
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class TimeEventHandler(
    private val inGameTimeEventRepository: InGameTimeEventRepository
) {
    companion object {
        private val logger = LoggingUtils.getLogger<TimeEventHandler>()
    }

    @Transactional
    fun createEvent(
        game: InRamGame,
        eventType: InGameTimeEventType,
        sourceObject: WithTrueIngameId? = null,
        targetObject: WithTrueIngameId? = null,
        location: Location? = null,
        timeLeft: Long? = null
    ) {
        createEvent(
            gameId = game.gameId,
            eventType = eventType,
            startDateTime = game.globalTimer,
            sourceObject = sourceObject,
            targetObject = targetObject,
            location = location,
            timeLeft = timeLeft
        )
    }

    @Transactional
    fun createEvent(
        game: GameSession,
        eventType: InGameTimeEventType,
        startDateTime: Long,
        sourceUser: InGameUser? = null,
        targetUser: InGameUser? = null,
        location: Location? = null,
        timeLeft: Long? = null
    ) {
        createEvent(
            gameId = game.id!!,
            eventType = eventType,
            startDateTime = startDateTime,
            sourceObject = sourceUser,
            targetObject = targetUser,
            location = location,
            timeLeft = timeLeft
        )
    }

    @Transactional
    fun createEvent(
        gameId: Long,
        eventType: InGameTimeEventType,
        startDateTime: Long,
        sourceObject: WithTrueIngameId? = null,
        targetObject: WithTrueIngameId? = null,
        location: Location? = null,
        timeLeft: Long? = null
    ) {
        createEvent(
            gameId = gameId,
            eventType = eventType,
            startDateTime = startDateTime,
            sourceObjectId = sourceObject?.inGameId(),
            targetObjectId = targetObject?.inGameId(),
            location = location,
            timeLeft = timeLeft
        )
    }

    @Transactional
    fun createEvent(
        gameId: Long,
        eventType: InGameTimeEventType,
        startDateTime: Long,
        sourceObjectId: Long? = null,
        targetObjectId: Long? = null,
        location: Location? = null,
        timeLeft: Long? = null
    ) {
        val timer = InGameTimeEvent(
            id = generateRandomId(),
            gameId = gameId,
            timeStart = startDateTime,
            timePast = 0L,
            timeLeft = timeLeft ?: eventType.getDefaultTime(),
            sourceObjectId = sourceObjectId,
            targetObjectId = targetObjectId,
            type = eventType,
            state = InGameTimeEventState.ACTIVE,
            xLocation = location?.x,
            yLocation = location?.y,
            zLocation = location?.z,
            visibilityModifiers = setOf(VisibilityModifier.ALL)
        )
        inGameTimeEventRepository.save(timer)
    }

    @Transactional
    fun tryToDeleteEvent(
        eventType: InGameTimeEventType,
        allEvents: List<InGameTimeEvent>
    ) {
        logger.info("deleting ${eventType.name} event")
        allEvents.filter {
            it.type == eventType
        }.forEach {
            it.timePast += it.timeLeft
            it.timeLeft = 0
            it.state = InGameTimeEventState.PAST
            inGameTimeEventRepository.delete(it)
        }
    }

    @Transactional
    fun pushEvent(event: InGameTimeEvent, timeToAdd: Long) {
        event.timePast += timeToAdd
        event.timeLeft -= timeToAdd
        inGameTimeEventRepository.save(event)
    }
}