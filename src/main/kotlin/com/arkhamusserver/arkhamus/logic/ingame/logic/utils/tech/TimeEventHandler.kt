package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech

import com.arkhamusserver.arkhamus.logic.ingame.logic.Location
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisTimeEventRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisTimeEvent
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithTrueIngameId
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class TimeEventHandler(
    private val redisTimeEventRepository: RedisTimeEventRepository
) {
    companion object {
        var logger: Logger = LoggerFactory.getLogger(TimeEventHandler::class.java)
    }

    @Transactional
    fun createEvent(
        game: RedisGame,
        eventType: RedisTimeEventType,
        sourceObject: WithTrueIngameId? = null,
        targetObject: WithTrueIngameId? = null,
        location: Location? = null,
        timeLeft: Long? = null
    ) {
        createEvent(
            gameId = game.gameId!!,
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
        eventType: RedisTimeEventType,
        startDateTime: Long,
        sourceUser: RedisGameUser? = null,
        targetUser: RedisGameUser? = null,
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
        eventType: RedisTimeEventType,
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
        eventType: RedisTimeEventType,
        startDateTime: Long,
        sourceObjectId: Long? = null,
        targetObjectId: Long? = null,
        location: Location? = null,
        timeLeft: Long? = null
    ) {
        val timer = RedisTimeEvent(
            id = generateRandomId(),
            gameId = gameId,
            timeStart = startDateTime,
            timePast = 0L,
            timeLeft = timeLeft ?: eventType.getDefaultTime(),
            sourceObjectId = sourceObjectId,
            targetObjectId = targetObjectId,
            type = eventType,
            state = RedisTimeEventState.ACTIVE,
            xLocation = location?.x,
            yLocation = location?.y,
            zLocation = location?.z,
            visibilityModifiers = setOf(VisibilityModifier.ALL)
        )
        redisTimeEventRepository.save(timer)
    }

    @Transactional
    fun tryToDeleteEvent(
        eventType: RedisTimeEventType,
        allEvents: List<RedisTimeEvent>
    ) {
        logger.info("deleting ${eventType.name} event")
        allEvents.filter {
            it.type == eventType
        }.forEach {
            it.timePast += it.timeLeft
            it.timeLeft = 0
            it.state = RedisTimeEventState.PAST
            redisTimeEventRepository.delete(it)
        }
    }

    @Transactional
    fun pushEvent(event: RedisTimeEvent, timeToAdd: Long) {
        event.timePast += timeToAdd
        event.timeLeft -= timeToAdd
        redisTimeEventRepository.save(event)
    }
}