package com.arkhamusserver.arkhamus.logic.ingame.logic.visibility

import kotlin.math.sqrt

interface VisibilityPoint {
    val x: Double
    val z: Double
}

fun VisibilityPoint.distanceTo(other: VisibilityPoint): Double {
    return sqrt((x - other.x) * (x - other.x) + (z - other.z) * (z - other.z))
}

data class Observer(override val x: Double, override val z: Double, val range: Double): VisibilityPoint