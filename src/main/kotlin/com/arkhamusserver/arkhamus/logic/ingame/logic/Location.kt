package com.arkhamusserver.arkhamus.logic.ingame.logic

import com.arkhamusserver.arkhamus.model.redis.interfaces.WithPoint

data class Location(
    var x: Double,
    var y: Double,
    var z: Double,
): WithPoint {
    override fun x(): Double = x
    override fun y(): Double = y
    override fun z(): Double = z
}