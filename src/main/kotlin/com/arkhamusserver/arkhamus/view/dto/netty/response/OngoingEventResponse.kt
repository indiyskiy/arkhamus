package com.arkhamusserver.arkhamus.view.dto.netty.response

import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType

data class OngoingEventResponse (
    val type: RedisTimeEventType
)