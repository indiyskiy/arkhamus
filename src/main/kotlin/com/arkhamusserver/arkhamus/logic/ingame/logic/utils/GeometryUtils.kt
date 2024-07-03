package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import org.springframework.stereotype.Component
import java.awt.geom.Point2D

@Component
class GeometryUtils {

    fun distance(
        point1X: Double,
        point1Y: Double,
        point2X: Double,
        point2Y: Double
    ): Double {
        return Point2D.Double(point1X, point1Y).distance(Point2D.Double(point2X, point2Y))
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