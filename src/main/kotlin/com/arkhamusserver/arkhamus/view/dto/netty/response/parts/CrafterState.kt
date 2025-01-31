package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapObjectState
import com.arkhamusserver.arkhamus.model.ingame.InGameCrafter

data class CrafterState(
    var crafterId: Long,
    var state: MapObjectState,
    var holdingUserId: Long?,
    var gameTags: Set<String>
) {
    constructor(crafter: InGameCrafter) : this(
        crafterId = crafter.inGameId(),
        state = crafter.state,
        holdingUserId = crafter.holdingUser,
        gameTags = crafter.gameTags().map { it.name }.toSet()
    )
}