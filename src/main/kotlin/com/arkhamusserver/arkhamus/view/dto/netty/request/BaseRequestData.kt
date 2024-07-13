package com.arkhamusserver.arkhamus.view.dto.netty.request

import com.arkhamusserver.arkhamus.model.redis.WithPoint


data class BaseRequestData(
    val tick: Long,
    val userPosition: UserPosition
)

data class UserPosition(
    val x: Double,
    val y: Double
): WithPoint {
    override fun x(): Double {
        return x
    }

    override fun y(): Double {
        return y
    }
}