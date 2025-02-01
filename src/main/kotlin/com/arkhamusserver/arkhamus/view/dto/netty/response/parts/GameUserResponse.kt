package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

import com.arkhamusserver.arkhamus.model.enums.ingame.tag.UserStateTag
import com.arkhamusserver.arkhamus.model.ingame.InGameUser

data class GameUserResponse(
    var id: Long,
    var nickName: String?,
    var x: Double?,
    var y: Double?,
    var z: Double?,
    var stateTags: Set<UserStateTag> = emptySet(),
) {
    constructor(gameUser: InGameUser) : this(
        id = gameUser.inGameId(),
        nickName = gameUser.nickName,
        x = gameUser.x,
        y = gameUser.y,
        z = gameUser.z,
        stateTags = gameUser.stateTags
    )
}