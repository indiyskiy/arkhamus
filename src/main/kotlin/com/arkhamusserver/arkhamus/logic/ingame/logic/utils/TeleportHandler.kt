package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.WithPoint
import org.springframework.stereotype.Component

@Component
class TeleportHandler(
    private val timeEventHandler: TimeEventHandler
) {
    fun forceTeleport(
        game: RedisGame,
        user: RedisGameUser,
        point: WithPoint?
    ) {
        point?.let {
            user.x = point.x()
            user.y = point.y()
            timeEventHandler.createEvent(
                game = game,
                eventType = RedisTimeEventType.TELEPORTATION_STUN,
                sourceUser = null,
                targetUser = user,
                location = user.x to user.y,
                timeLeft = RedisTimeEventType.TELEPORTATION_STUN.getDefaultTime()
            )
        }
    }
}