package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.model.redis.WithPoint
import org.springframework.stereotype.Component
import java.awt.geom.Point2D

@Component
class GeometryUtils {

    fun distanceLessOrEquals(
        withPoint1: WithPoint,
        withPoint2: WithPoint,
        maxDistance: Double
    ): Boolean {
        return distanceLessOrEquals(withPoint1.x(), withPoint1.z(), withPoint2.x(), withPoint2.z(), maxDistance)
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

    fun lineIntersects(a: Line, b: Line): Boolean {
        val d = lineInteractionD(a, b)
        return d != 0.0
    }

    fun lineIntersection(a: Line, b: Line): Point? {
        val d = lineInteractionD(a, b)
        if (d == 0.0) return null

        val xi = (magicNumber1(b, a) - magicNumber1(a, b)) / d
        val yi = (magicNumber2(b, a) - magicNumber2(a, b)) / d
        return Point(xi, yi)
    }

    private fun lineInteractionD(
        a: Line,
        b: Line
    ) = (a.p1.x - a.p2.x) * (b.p1.y - b.p2.y) - (a.p1.y - a.p2.y) * (b.p1.x - b.p2.x)

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

    private fun magicNumber2(
        b: Line,
        a: Line
    ) = (b.p1.y - b.p2.y) * (a.p1.x * a.p2.y - a.p1.y * a.p2.x)

    private fun magicNumber1(
        b: Line,
        a: Line
    ) = (b.p1.x - b.p2.x) * (a.p1.x * a.p2.y - a.p1.y * a.p2.x)

    private fun det(a: Point, b: Point, c: Point) =
        a.x * (b.y - c.y) +
                b.x * (c.y - a.y) +
                c.x * (a.y - b.y)

    fun nearestPoint(point: WithPoint, points: List<WithPoint>?):WithPoint? {
        if(points == null || points.isEmpty()) return null
        return points.minByOrNull { distance(it, point) }
    }

    data class Point(var x: Double, var y: Double)

    class Line(var p1: Point, var p2: Point)

    class Tetragon(val p0: Point, val p1: Point, val p2: Point, val p3: Point)

    class Ellipse(val center: Point, val rz: Double, val rx: Double)
}

