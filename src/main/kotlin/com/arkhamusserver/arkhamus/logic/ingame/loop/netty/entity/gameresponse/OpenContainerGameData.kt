package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser

class OpenContainerGameData(
    var container: RedisContainer,
    gameUser: RedisGameUser,
    otherGameUsers: List<RedisGameUser>,
    visibleOngoingEvents: List<OngoingEvent>,
    tick: Long
) : GameUserData(gameUser, otherGameUsers, visibleOngoingEvents, tick)