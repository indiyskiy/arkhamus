package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapObjectState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.InGameObjectTag
import com.arkhamusserver.arkhamus.model.redis.RedisCrafter

data class CrafterState(
    var crafterId: Long,
    var state: MapObjectState,
    var holdingUserId: Long?,
    var gameTags: List<InGameObjectTag>
) {
    constructor(crafter: RedisCrafter) : this(
        crafterId = crafter.inGameId(),
        state = crafter.state,
        holdingUserId = crafter.holdingUser,
        gameTags = crafter.gameTags()
    )
}