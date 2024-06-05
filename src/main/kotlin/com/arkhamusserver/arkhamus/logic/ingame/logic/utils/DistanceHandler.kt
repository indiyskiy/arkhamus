package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import org.springframework.stereotype.Component
import kotlin.math.hypot

@Component
class DistanceHandler {

    fun distance(
        point1X: Double,
        point1Y: Double,
        point2X: Double,
        point2Y: Double
    ): Double {
        return hypot(point1X - point2X, point1Y - point2Y)
    }

    fun distanceLessOrEquals(
        point1X: Double,
        point1Y: Double,
        point2X: Double,
        point2Y: Double,
        maxDistance: Double
    ): Boolean {
        return maxDistance >= distance(point1X, point1Y, point2X, point2Y)
    }
}