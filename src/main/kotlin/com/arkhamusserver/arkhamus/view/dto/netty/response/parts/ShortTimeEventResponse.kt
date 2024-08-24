package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.enums.ingame.ShortTimeEventType

data class ShortTimeEventResponse(
    var id: String,

    var sourceId: Long? = null,

    var sourceType: GameObjectType,

    var xLocation: Double? = null,
    var yLocation: Double? = null,

    var timeStart: Long,
    var timePast: Long,
    var timeLeft: Long,

    var type: ShortTimeEventType,
)