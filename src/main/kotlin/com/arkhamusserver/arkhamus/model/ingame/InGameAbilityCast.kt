package com.arkhamusserver.arkhamus.model.ingame

import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.InGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.InGameTimeEventState

data class InGameAbilityCast(
    override var id: String,
    override var gameId: Long,
    var abilityId: Int,
    var sourceUserId: Long? = null,
    var targetId: String? = null,
    var targetType: GameObjectType? = null,
    var timeStart: Long,
    var timePast: Long,
    var timeLeftCooldown: Long,
    var timeLeftActive: Long,
    var state: InGameTimeEventState,
    var xLocation: Double? = null,
    var yLocation: Double? = null,
) : InGameEntity