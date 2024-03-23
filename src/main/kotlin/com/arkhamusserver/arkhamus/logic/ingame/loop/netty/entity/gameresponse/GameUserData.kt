package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser

open class GameUserData(
    val gameUser: RedisGameUser?,
    val otherGameUsers: List<RedisGameUser> = emptyList(),
    val visibleOngoingEvents: List<OngoingEvent>,
    tick: Long
) : RequestProcessData(tick)