package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithPoint
import org.springframework.stereotype.Component
import java.awt.geom.Point2D

@Component
class GeometryUtils(
    private val globalGameSettings: GlobalGameSettings
) {

    fun distanceLessOrEquals(
        withPoint1: WithPoint,
        withPoint2: WithPoint,
        maxDistance: Double?
    ): Boolean {
        return maxDistance?.let {
            distanceLessOrEquals(withPoint1.x(), withPoint1.z(), withPoint2.x(), withPoint2.z(), maxDistance)
        } == true
    }

    fun distance(
        withPoint1: WithPoint,
        withPoint2: WithPoint,
    ): Double {
        return distance(withPoint1.x(), withPoint1.z(), withPoint2.x(), withPoint2.z())
    }

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

    fun contains(tetragon: Tetragon, point: WithPoint): Boolean =
        contains(tetragon, Point(point.x(), point.z()))

    fun contains(ellipse: Ellipse, point: WithPoint): Boolean =
        contains(ellipse, Point(point.x(), point.z()))

    fun contains(tetragon: Tetragon, point: Point): Boolean =
        det(tetragon.p0, tetragon.p1, point) >= 0 &&
                det(tetragon.p1, tetragon.p2, point) >= 0 &&
                det(tetragon.p2, tetragon.p3, point) >= 0 &&
                det(tetragon.p3, tetragon.p0, point) >= 0

    fun contains(ellipse: Ellipse, point: Point): Boolean {
        val dx = ellipse.center.x - point.x
        val dy = ellipse.center.y - point.y
        return (dx * dx) / (ellipse.rx * ellipse.rx) +
                (dy * dy) / (ellipse.rz * ellipse.rz) <= 1
    }

    fun onHighGround(
        whoLooks: InGameUser,
        target: WithPoint
    ): Boolean {
        return (target.y() - whoLooks.y) >= globalGameSettings.highGroundHeight
    }

    private fun det(a: Point, b: Point, c: Point) =
        a.x * (b.y - c.y) +
                b.x * (c.y - a.y) +
                c.x * (a.y - b.y)

    fun <T : WithPoint> nearestPoint(point: WithPoint, points: List<T>?): T? {
        if (points == null || points.isEmpty()) return null
        return points.minByOrNull { distance(it, point) }
    }

    data class Point(var x: Double, var y: Double)

    class Tetragon(
        val p0: Point,
        val p1: Point,
        val p2: Point,
        val p3: Point,
        override val height: Double = 0.0
    ): WithHeight

    class Ellipse(
        val center: Point,
        val rz: Double,
        val rx: Double,
        override val height: Double = 0.0
    ): WithHeight

    interface WithHeight { val height: Double }
}