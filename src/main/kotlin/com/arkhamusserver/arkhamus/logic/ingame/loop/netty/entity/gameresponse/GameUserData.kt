package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser

open class GameUserData(
    val gameUser: RedisGameUser?,
    val otherGameUsers: List<RedisGameUser> = emptyList(),
    val visibleOngoingEffects: List<OngoingEvent>,
    tick: Long
) : GameData(tick)