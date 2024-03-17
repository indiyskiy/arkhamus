package com.arkhamusserver.arkhamus.logic.ingame.logic

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.springframework.stereotype.Component

@Component
class UserLocationHandler(
    private val distanceHandler: DistanceHandler
) {
    private val testLamp = Lamp(100.0, 100.0, 50.0)
    fun isInDarkness(user: RedisGameUser, globalGameData: GlobalGameData): Boolean {
        return distanceHandler.distanceLessOrEquals(
            point1X = user.x,
            point1Y = user.y,
            point2X = testLamp.x,
            point2Y = testLamp.y,
            testLamp.lightDistance
        )
    }

    data class Lamp(
        val x: Double,
        val y: Double,
        val lightDistance: Double
    )
}