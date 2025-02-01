package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

import com.arkhamusserver.arkhamus.model.ingame.InGameUser

data class EndOfGameUserResponse(
    val id: Long,
    val nickName: String?,
    val role: String?,
    val classInGame: String?,
) {
    constructor(gameUser: InGameUser) : this(
        id = gameUser.inGameId(),
        nickName = gameUser.nickName,
        role = gameUser.role.name,
        classInGame = gameUser.classInGame.name,
    )
}