package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.ingame.logic.Location
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.TimeEventHandler
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.UserStateTag
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithPoint
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class TeleportHandler(
    private val timeEventHandler: TimeEventHandler
) {

    companion object {
        private val logger = LoggerFactory.getLogger(TeleportHandler::class.java)
    }

    fun forceTeleport(
        game: RedisGame,
        user: RedisGameUser,
        point: WithPoint
    ) {
        user.x = point.x()
        user.y = point.y()
        user.z = point.z()
        user.stateTags+=UserStateTag.STUN
        timeEventHandler.createEvent(
            game = game,
            eventType = RedisTimeEventType.TELEPORTATION_STUN,
            sourceObject = null,
            targetObject = user,
            location = Location(user.x, user.y, user.z),
            timeLeft = RedisTimeEventType.TELEPORTATION_STUN.getDefaultTime()
        )
        logger.info("user ${user.inGameId()} teleported to ${user.x()}; ${user.y()}; ${user.z()}")
    }
}