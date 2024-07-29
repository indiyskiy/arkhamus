package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

import com.arkhamusserver.arkhamus.model.redis.RedisGameUser

data class EndOfGameUserResponse(
    val id: Long,
    val nickName: String?,
    val role: String?,
    val classInGame: String?,
) {
    constructor(gameUser: RedisGameUser) : this(
        id = gameUser.userId,
        nickName = gameUser.nickName,
        role = gameUser.role.name,
        classInGame = gameUser.classInGame.name,
    )
}