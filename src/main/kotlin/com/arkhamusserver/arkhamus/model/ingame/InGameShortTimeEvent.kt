package com.arkhamusserver.arkhamus.model.ingame

import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.InGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.ShortTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.InGameTimeEventState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithVisibilityModifiers

data class InGameShortTimeEvent(
    override var id: String,
    override var gameId: Long,

    var sourceId: Long? = null,
    var objectId: Long? = null,

    var xLocation: Double? = null,
    var yLocation: Double? = null,
    var zLocation: Double? = null,

    var timeStart: Long,
    var timePast: Long,
    var timeLeft: Long,

    var type: ShortTimeEventType,
    var state: InGameTimeEventState,

    var visibilityModifiers: MutableSet<VisibilityModifier>,
    var additionalData: Any? = null,
) : InGameEntity, WithVisibilityModifiers {
    override fun visibilityModifiers(): MutableSet<VisibilityModifier> {
        return visibilityModifiers
    }
}