package com.arkhamusserver.arkhamus.model.ingame

import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.InGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.InGameTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.InGameTimeEventState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithVisibilityModifiers

data class InGameTimeEvent(
    override var id: String,
    override var gameId: Long,
    var sourceObjectId: Long? = null,
    var targetObjectId: Long? = null,
    var timeStart: Long,
    var timePast: Long,
    var timeLeft: Long,
    var type: InGameTimeEventType,
    var state: InGameTimeEventState,
    var xLocation: Double? = null,
    var yLocation: Double? = null,
    var zLocation: Double? = null,
    var visibilityModifiers: Set<VisibilityModifier>,
) : InGameEntity, WithVisibilityModifiers {
    override fun visibilityModifiers(): Set<VisibilityModifier> {
        return visibilityModifiers
    }
}