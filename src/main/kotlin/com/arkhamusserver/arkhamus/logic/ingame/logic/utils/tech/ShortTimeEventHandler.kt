package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech

import com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.shortTime.SpecificShortTimeEventFilter
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisShortTimeEventRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.ShortTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.RedisTimeEventState
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
        val filteredByState = events.filter { it.timeLeft > 0 && it.state == RedisTimeEventState.ACTIVE }
        val filteredByObject = filteredByState.filter {
            it.sourceId == null || canSeeTarget(
                it,
                user,
                data
            )
        }
        val filterByPosition = filteredByObject.filter {
            canSeeLocation(
                it,
                user,
                data
            )
        }
        val filteredByAdditionalFilters = filterByPosition.filter {
            specificShortTimeEventFilters.firstOrNull { filter ->
                filter.accept(it)
            }?.canSee(
                it,
                user,
                zones,
                data
            ) != false
        }
        return filteredByAdditionalFilters
    }

    @Transactional
    fun createShortTimeEvent(
        objectId: Long,
        gameId: Long,
        globalTimer: Long,
        type: ShortTimeEventType,
        visibilityModifiers: Set<String>,
        data: GlobalGameData
    ) {
        val event = RedisShortTimeEvent(
            id = generateRandomId(),
            gameId = gameId,
            sourceId = objectId,
            xLocation = null,
            yLocation = null,
            timeStart = globalTimer,
            timePast = 0,
            timeLeft = type.getTime(),
            type = type,
            state = RedisTimeEventState.ACTIVE,
            visibilityModifiers = visibilityModifiers.map { it }.toMutableSet()
        )
        redisShortTimeEventRepository.save(event)
        data.shortTimeEvents += event
    }

    private fun canSeeTarget(
        event: RedisShortTimeEvent,
        user: RedisGameUser,
        data: GlobalGameData
    ): Boolean {
        val target = finder.findById(event.sourceId.toString(), event.type.getSource(), data)
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

