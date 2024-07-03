package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisLantern
import org.springframework.stereotype.Component

@Component
class UserLocationHandler(
    private val geometryUtils: GeometryUtils
) {
    fun isInDarkness(user: RedisGameUser, globalGameData: GlobalGameData): Boolean {
        return !nearLantern(user, globalGameData.lanterns.values)
    }

    private fun nearLantern(user: RedisGameUser, lanterns: Collection<RedisLantern>): Boolean {
        return lanterns.any {
            geometryUtils.distanceLessOrEquals(
                point1X = user.x,
                point1Y = user.y,
                point2X = it.x,
                point2Y = it.y,
                it.lightRange
            )
        }
    }
}