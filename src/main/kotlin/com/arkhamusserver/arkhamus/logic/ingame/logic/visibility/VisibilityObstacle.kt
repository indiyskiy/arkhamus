package com.arkhamusserver.arkhamus.logic.ingame.logic.visibility

import com.arkhamusserver.arkhamus.model.database.entity.game.VisibilityDoor
import com.arkhamusserver.arkhamus.model.database.entity.game.VisibilityWall
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithPoint
import kotlin.math.abs

sealed interface VisibilityObstacle {
    fun blocksVision(from: WithPoint, to: WithPoint): Boolean

    fun intersectsRect(xMin: Double, zMin: Double, xMax: Double, zMax: Double): Boolean

    fun blocksVisionFor(observer: RedisGameUser): Boolean

    companion object {
        fun fromWall(wall: VisibilityWall): VisibilityObstacle = Interval(
            x1 = wall.x1,
            z1 = wall.z1,
            x2 = wall.x2,
            z2 = wall.z2
        ) { _ -> true }

        fun fromDoor(door: VisibilityDoor): VisibilityObstacle = Interval(
            x1 = door.x1,
            z1 = door.z1,
            x2 = door.x2,
            z2 = door.z2
            // TODO actually check if the door is closed for the user
        ) { gameUser -> false }
    }
}

data class Interval(val x1: Double, val z1: Double, val x2: Double, val z2: Double, val blockVisionChecker: (RedisGameUser) -> Boolean ): VisibilityObstacle {
    override fun blocksVision(from: WithPoint, to: WithPoint): Boolean {
        if (from is RedisGameUser && !blockVisionChecker(from)) return false
        // replicating solution from https://en.wikipedia.org/wiki/Intersection_(geometry)#Two_line_segments
        val x3 = from.x()
        val z3 = from.z()
        val x4 = to.x()
        val z4 = to.z()
        val a1 = x2 - x1
        val b1 = x3 - x4
        val c1 = x3 - x1
        val a2 = z2 - z1
        val b2 = z3 - z4
        val c2 = z3 - z1
        val divider = a1 * b2 - a2 * b1
        // TODO better accuracy?
        if (abs(divider) < 0.005) return false
        val s0 = (c1 * b2 - c2 * b1) / divider
        val t0 = (a1 * c2 - a2 * c1) / divider
        val res = s0 in 0.0..1.0 && t0 in 0.0 .. 1.0
        return res
    }

    override fun intersectsRect(xMin: Double, zMin: Double, xMax: Double, zMax: Double): Boolean {
        // check if it's inside rectangle
        if (xMin <= x1 && xMin <= x2 && xMax >= x1 && xMax >= x2 && zMin <= z1 && zMin <= z2 && zMax >= z1 && zMax >= z2) {
            return true
        }
        // check if ends of interval are in different semiplanes with rect sides, at least 2 different semiplanes => intersects
        // (xMin, yMin) - (xMax, yMin)
        val marker1 = if ((z1 - zMin) * (z2 - zMin) >= 0) 1 else 0
        // (xMin, yMax) - (xMax, yMax)
        val marker2 = if ((z1 - zMax) * (z2 - zMax) >= 0) 1 else 0
        // (xMin, yMin) - (xMin, yMax)
        val marker3 = if ((x1 - xMin) * (x2 - xMin) >= 0) 1 else 0
        // (xMax, yMin) - (xMax, yMax)
        val marker4 = if ((x1 - xMax) * (x2 - xMax) >= 0) 1 else 0
        return marker1 + marker2 + marker3 + marker4 >= 2
    }

    override fun blocksVisionFor(observer: RedisGameUser) = blockVisionChecker(observer)
}