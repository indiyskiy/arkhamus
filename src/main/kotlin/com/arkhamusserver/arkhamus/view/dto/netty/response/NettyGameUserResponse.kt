package com.arkhamusserver.arkhamus.view.dto.netty.response

import com.arkhamusserver.arkhamus.model.redis.RedisGameUser

data class NettyGameUserResponse(
    val id: Long,
    val nickName: String?,
    val x: Double,
    val y: Double,
    val stateTags: Set<String> = emptySet(),
) {
    constructor(gameUser: RedisGameUser) : this(
        id = gameUser.userId,
        nickName = gameUser.nickName,
        x = gameUser.x,
        y = gameUser.y,
        stateTags = gameUser.stateTags
    )
}