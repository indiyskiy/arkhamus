package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType

data class OngoingEventResponse(
    var id: String,
    var sourceObjectId: Long?,
    var targetObjectId: Long?,
    var sourceObjectType:GameObjectType?,
    var targetObjectType: GameObjectType?,
    var timeStart: Long,
    var timePast: Long,
    var timeLeft: Long,
    var type: RedisTimeEventType,
    var state: RedisTimeEventState,
    var xLocation: Double?,
    var yLocation: Double?,
    var zLocation: Double?,
) {
    constructor(ongoingEvent: OngoingEvent) : this(
        id = ongoingEvent.event.id,
        sourceObjectId = ongoingEvent.event.sourceObjectId,
        targetObjectId = ongoingEvent.event.targetObjectId,
        sourceObjectType = ongoingEvent.event.type.getSourceType(),
        targetObjectType = ongoingEvent.event.type.getTargetType(),
        timeStart = ongoingEvent.event.timeStart,
        timePast = ongoingEvent.event.timePast,
        timeLeft = ongoingEvent.event.timeLeft,
        type = ongoingEvent.event.type,
        state = ongoingEvent.event.state,
        xLocation = ongoingEvent.event.xLocation,
        yLocation = ongoingEvent.event.yLocation,
        zLocation = ongoingEvent.event.zLocation
    )
}