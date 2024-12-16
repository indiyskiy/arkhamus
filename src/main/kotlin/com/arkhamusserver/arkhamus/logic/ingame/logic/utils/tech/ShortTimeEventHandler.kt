package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech

import com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.shortTime.SpecificShortTimeEventFilter
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisShortTimeEventRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.enums.ingame.ShortTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.Visibility
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisShortTimeEvent
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithPoint
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ShortTimeEventHandler(
    private val specificShortTimeEventFilters: List<SpecificShortTimeEventFilter>,
    private val redisShortTimeEventRepository: RedisShortTimeEventRepository,
    private val userLocationHandler: UserLocationHandler,
    private val finder: GameObjectFinder
) {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(ShortTimeEventHandler::class.java)
    }

    fun filter(
        events: List<RedisShortTimeEvent>,
        user: RedisGameUser,
        zones: List<LevelZone>,
        data: GlobalGameData
    ): List<RedisShortTimeEvent> {
        val filteredByState = events.filter { filterByState(it) }
        val filteredByObject = filteredByState.filter { filterByObject(it, user, data) }
        val filterByPosition = filteredByObject.filter { canSeeLocation(it, user, data) }
        val filteredByVisibility = filterByPosition.filter { filterByVisibility(it, user) }
        val filteredByAdditionalFilters = filteredByVisibility.filter { byAdditionalFilters(it, user, zones, data) }
        return filteredByAdditionalFilters
    }

    private fun filterByState(event: RedisShortTimeEvent): Boolean =
        event.timeLeft > 0 && event.state == RedisTimeEventState.ACTIVE

    private fun filterByObject(
        event: RedisShortTimeEvent,
        user: RedisGameUser,
        data: GlobalGameData
    ): Boolean = event.objectId == null || canSeeTarget(event, user, data)

    private fun byAdditionalFilters(
        event: RedisShortTimeEvent,
        user: RedisGameUser,
        zones: List<LevelZone>,
        data: GlobalGameData
    ): Boolean = specificShortTimeEventFilters.firstOrNull { filter ->
        filter.accept(event)
    }?.canSee(event, user, zones, data) != false

    private fun filterByVisibility(
        event: RedisShortTimeEvent,
        user: RedisGameUser
    ): Boolean {
        return when (event.type.getVisibility()) {
            Visibility.NONE -> false
            Visibility.TARGET -> user.inGameId() == event.objectId && event.type.getSource() == GameObjectType.CHARACTER
            Visibility.PUBLIC -> true
            Visibility.SOURCE, Visibility.SOURCE_AND_TARGET -> {
                logger.info("filter by source: ${event.sourceId} == ${user.inGameId()}")
                if( event.sourceId != user.inGameId()){
                    logger.info("$event")
                }
                event.sourceId == user.inGameId()
            }
        }
    }

    @Transactional
    fun createShortTimeEvent(
        objectId: Long,
        gameId: Long,
        globalTimer: Long,
        type: ShortTimeEventType,
        visibilityModifiers: Set<VisibilityModifier>,
        data: GlobalGameData,
        sourceUserId: Long? = null,
        additionalData: Any? = null
    ) {
        val event = RedisShortTimeEvent(
            id = generateRandomId(),
            gameId = gameId,
            sourceId = sourceUserId,
            objectId = objectId,
            xLocation = null,
            yLocation = null,
            timeStart = globalTimer,
            timePast = 0,
            timeLeft = type.getTime(),
            type = type,
            state = RedisTimeEventState.ACTIVE,
            visibilityModifiers = visibilityModifiers.map { it }.toMutableSet(),
            additionalData = additionalData
        )
        redisShortTimeEventRepository.save(event)
        data.shortTimeEvents += event
    }

    private fun canSeeTarget(
        event: RedisShortTimeEvent,
        user: RedisGameUser,
        data: GlobalGameData
    ): Boolean {
        val target = finder.findById(event.objectId.toString(), event.type.getSource(), data)
        if (target == null) {
            return false
        }
        if (target is WithPoint) {
            return userLocationHandler.userCanSeeTarget(user, target, data.levelGeometryData, true)
        }
        return true
    }

    private fun canSeeLocation(
        event: RedisShortTimeEvent,
        user: RedisGameUser,
        data: GlobalGameData
    ): Boolean {
        if (event.xLocation == null || event.yLocation == null || event.zLocation == null) return true
        return userLocationHandler.userCanSeeTarget(
            user,
            ShortTimeEventLocation(
                event.xLocation!!,
                event.yLocation!!,
                event.zLocation!!
            ),
            data.levelGeometryData,
            true
        )
    }

    data class ShortTimeEventLocation(
        private val x: Double,
        private val y: Double,
        private val z: Double,
    ) : WithPoint {
        override fun x(): Double {
            return x
        }

        override fun y(): Double {
            return y
        }

        override fun z(): Double {
            return z
        }
    }
}

