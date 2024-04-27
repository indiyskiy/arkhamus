package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.AbilityOfUserResponse

open class GameUserData(
    val gameUser: RedisGameUser?,
    val otherGameUsers: List<RedisGameUser> = emptyList(),
    val visibleOngoingEvents: List<OngoingEvent>,
    val availableAbilities: List<AbilityOfUserResponse>,
    tick: Long
) : RequestProcessData(tick)