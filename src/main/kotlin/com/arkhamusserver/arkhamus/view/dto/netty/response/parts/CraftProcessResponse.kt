package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.InGameTimeEventState
import com.arkhamusserver.arkhamus.model.ingame.InGameCraftProcess

data class CraftProcessResponse(
    var id: String,
    var recipeId: Int,
    var targetCrafterId: Long,
    var sourceUserId: Long?,
    var timeStart: Long,
    var timePast: Long,
    var timeLeft: Long,
    var state: InGameTimeEventState,
) {
    constructor(craftProcess: InGameCraftProcess) : this(
        id = craftProcess.id,
        recipeId = craftProcess.recipeId,
        targetCrafterId = craftProcess.targetCrafterId,
        sourceUserId = craftProcess.sourceUserId,
        timeStart = craftProcess.timeStart,
        timePast = craftProcess.timePast,
        timeLeft = craftProcess.timeLeft,
        state = craftProcess.state,
    )
}