package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech

import com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.shortTime.SpecificShortTimeEventFilter
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameShortTimeEventRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.enums.ingame.ShortTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.Visibility
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.InGameTimeEventState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.InGameShortTimeEvent
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithPoint
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ShortTimeEventHandler(
    private val specificShortTimeEventFilters: List<SpecificShortTimeEventFilter>,
    private val inGameShortTimeEventRepository: InGameShortTimeEventRepository,
    private val userLocationHandler: UserLocationHandler,
    private val finder: GameObjectFinder
) {

    companion object {
        private val logger = LoggingUtils.getLogger<ShortTimeEventHandler>()
    }

    fun filter(
        events: List<InGameShortTimeEvent>,
        user: InGameUser,
        zones: List<LevelZone>,
        data: GlobalGameData
    ): List<InGameShortTimeEvent> {
        val filteredByState = events.filter { filterByState(it) }
        val filteredByObject = filteredByState.filter { filterByObject(it, user, data) }
        val filterByPosition = filteredByObject.filter { canSeeLocation(it, user, data) }
        val filteredByVisibility = filterByPosition.filter { filterByVisibility(it, user) }
        val filteredByAdditionalFilters = filteredByVisibility.filter { byAdditionalFilters(it, user, zones, data) }
        return filteredByAdditionalFilters
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
        val event = InGameShortTimeEvent(
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
            state = InGameTimeEventState.ACTIVE,
            visibilityModifiers = visibilityModifiers.map { it }.toMutableSet(),
            additionalData = additionalData
        )
        inGameShortTimeEventRepository.save(event)
        data.shortTimeEvents += event
    }

    private fun filterByState(event: InGameShortTimeEvent): Boolean =
        event.timeLeft > 0 && event.state == InGameTimeEventState.ACTIVE

    private fun filterByObject(
        event: InGameShortTimeEvent,
        user: InGameUser,
        data: GlobalGameData
    ): Boolean = event.objectId == null || canSeeTarget(event, user, data)

    private fun byAdditionalFilters(
        event: InGameShortTimeEvent,
        user: InGameUser,
        zones: List<LevelZone>,
        data: GlobalGameData
    ): Boolean = specificShortTimeEventFilters.firstOrNull { filter ->
        filter.accept(event)
    }?.canSee(event, user, zones, data) != false

    private fun filterByVisibility(
        event: InGameShortTimeEvent,
        user: InGameUser
    ): Boolean {
        return when (event.type.getVisibility()) {
            Visibility.NONE -> false
            Visibility.TARGET -> user.inGameId() == event.objectId && event.type.getSource() == GameObjectType.CHARACTER
            Visibility.PUBLIC -> true
            Visibility.SOURCE, Visibility.SOURCE_OR_TARGET -> {
                event.sourceId == user.inGameId()
            }
        }
    }

    private fun canSeeTarget(
        event: InGameShortTimeEvent,
        user: InGameUser,
        data: GlobalGameData
    ): Boolean {
        val target = finder.findById(event.objectId.toString(), event.type.getSource(), data)
        if (target == null) {
            return false
        }
        if (target is WithPoint) {
            val canSee = userLocationHandler.userCanSeeTarget(user, target, data.levelGeometryData, true)
            return canSee
        }
        return true
    }

    private fun canSeeLocation(
        event: InGameShortTimeEvent,
        user: InGameUser,
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

