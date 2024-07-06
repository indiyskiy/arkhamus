package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType

data class OngoingEventResponse(
    var id: String,
    var sourceUserId: Long?,
    var targetUserId: Long?,
    var timeStart: Long,
    var timePast: Long,
    var timeLeft: Long,
    var type: RedisTimeEventType,
    var state: RedisTimeEventState,
    var xLocation: Double?,
    var yLocation: Double?,
) {
    constructor(ongoingEvent: OngoingEvent) : this(
        id = ongoingEvent.event.id,
        sourceUserId = ongoingEvent.event.sourceUserId,
        targetUserId = ongoingEvent.event.targetUserId,
        timeStart = ongoingEvent.event.timeStart,
        timePast = ongoingEvent.event.timePast,
        timeLeft = ongoingEvent.event.timeLeft,
        type = ongoingEvent.event.type,
        state = ongoingEvent.event.state,
        xLocation = ongoingEvent.event.xLocation,
        yLocation = ongoingEvent.event.yLocation
    )
}