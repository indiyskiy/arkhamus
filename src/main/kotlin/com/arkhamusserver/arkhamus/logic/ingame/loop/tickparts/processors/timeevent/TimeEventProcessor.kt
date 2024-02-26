package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.timeevent

import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.redis.RedisTimeEvent

interface TimeEventProcessor {
    fun accept(type: RedisTimeEventType): Boolean

    fun process(event: RedisTimeEvent, currentGameTime: Long)
    fun processStart(event: RedisTimeEvent, currentGameTime: Long)
    fun processEnd(event: RedisTimeEvent, currentGameTime: Long)
}