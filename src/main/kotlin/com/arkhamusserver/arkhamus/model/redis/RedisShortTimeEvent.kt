package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.enums.ingame.ShortTimeEventType
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisShortTimeEvent")
data class RedisShortTimeEvent(
    @Id var id: String,
    @Indexed var gameId: Long,

    var sourceId: Long? = null,

    var xLocation: Double? = null,
    var yLocation: Double? = null,

    var timeStart: Long,
    var timePast: Long,
    var timeLeft: Long,

    var type: ShortTimeEventType,
    var state: RedisTimeEventState,
)