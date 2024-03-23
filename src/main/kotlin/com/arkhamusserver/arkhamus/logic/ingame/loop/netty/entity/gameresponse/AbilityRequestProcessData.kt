package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.model.enums.ingame.Ability
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser

class AbilityRequestProcessData(
    val ability: Ability,
    val castedSuccessfully: Boolean,
    gameUser: RedisGameUser,
    otherGameUsers: List<RedisGameUser>,
    visibleOngoingEvents: List<OngoingEvent>,
    tick: Long
) : GameUserData(gameUser, otherGameUsers, visibleOngoingEvents, tick)