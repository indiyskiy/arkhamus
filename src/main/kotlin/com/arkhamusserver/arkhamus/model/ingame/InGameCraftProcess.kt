package com.arkhamusserver.arkhamus.model.ingame

import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.InGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.InGameTimeEventState

data class InGameCraftProcess(
    override var id: String,
    override var gameId: Long,
    var recipeId: Int,
    var targetCrafterId: Long,
    var sourceUserId: Long,
    var timeStart: Long,
    var timePast: Long,
    var timeLeft: Long,
    var state: InGameTimeEventState,
    var xLocation: Double? = null,
    var yLocation: Double? = null,
) : InGameEntity