package com.arkhamusserver.arkhamus.view.dto.netty.response

import com.arkhamusserver.arkhamus.model.redis.RedisGameUser

data class MyGameUserResponseMessage(
    val id: Long,
    val nickName: String,
    val madness: Double,
    val x: Double,
    val y: Double
) {
    constructor(gameUser: RedisGameUser) : this(
        id = gameUser.userId,
        nickName = gameUser.nickName,
        madness = gameUser.madness,
        x = gameUser.x,
        y = gameUser.y
    )
}