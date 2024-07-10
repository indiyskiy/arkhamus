package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

import com.arkhamusserver.arkhamus.model.redis.RedisGameUser

data class GameUserResponse(
    var id: Long,
    var nickName: String?,
    var x: Double?,
    var y: Double?,
    var stateTags: Set<String> = emptySet(),
) {
    constructor(gameUser: RedisGameUser) : this(
        id = gameUser.userId,
        nickName = gameUser.nickName,
        x = gameUser.x,
        y = gameUser.y,
        stateTags = gameUser.stateTags
    )
}