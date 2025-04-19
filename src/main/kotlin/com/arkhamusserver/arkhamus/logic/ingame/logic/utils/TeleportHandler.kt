package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.ingame.logic.Location
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.TimeEventHandler
import com.arkhamusserver.arkhamus.model.enums.ingame.InGameTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.UserStateTag
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.InRamGame
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithPoint
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import org.springframework.stereotype.Component

@Component
class TeleportHandler(
    private val timeEventHandler: TimeEventHandler
) {

    companion object {
        private val logger = LoggingUtils.getLogger<TeleportHandler>()
    }

    fun forceTeleport(
        game: InRamGame,
        user: InGameUser,
        point: WithPoint
    ) {
        user.x = point.x()
        user.y = point.y()
        user.z = point.z()
        user.stateTags += UserStateTag.STUN
        timeEventHandler.createEvent(
            game = game,
            eventType = InGameTimeEventType.TELEPORTATION_STUN,
            sourceObject = null,
            targetObject = user,
            location = Location(user.x, user.y, user.z),
            timeLeft = InGameTimeEventType.TELEPORTATION_STUN.getDefaultTime()
        )
        logger.info("user ${user.inGameId()} teleported to ${user.x()}; ${user.y()}; ${user.z()}")
    }
}