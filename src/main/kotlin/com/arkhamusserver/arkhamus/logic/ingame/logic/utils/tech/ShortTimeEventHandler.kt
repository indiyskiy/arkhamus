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
import com.fasterxml.uuid.Generators
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
        logger.info("filter ${events.size} short time events for user ${user.inGameId()}")
        val filteredByState = events.filter { it.timeLeft > 0 && it.state == RedisTimeEventState.ACTIVE }
        logger.info("filtered by state ${filteredByState.size}")
        val filteredByObject = filteredByState.filter {
            it.sourceId == null || canSeeTarget(
                it,
                user,
                data
            )
        }
        logger.info("filtered by object ${filteredByObject.size}")
        val filterByPosition = filteredByObject.filter {
            canSeeLocation(
                it,
                user,
                data
            )
        }
        logger.info("filtered by position ${filterByPosition.size}")
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
        logger.info("filtered by additional filters ${filteredByAdditionalFilters.size}")
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
        logger.info(
            "creating short time event ${type.name} " +
                    "visibility modifiers: ${visibilityModifiers.joinToString()} " +
                    "for object $objectId"
        )
        val event =  RedisShortTimeEvent(
            id = Generators.timeBasedEpochGenerator().generate().toString(),
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

