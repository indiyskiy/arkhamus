package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.ContainerCell

class CloseContainerGameData(
    var container: RedisContainer,
    var sortedInventory: List<ContainerCell>? = null,
    gameUser: RedisGameUser,
    otherGameUsers: List<RedisGameUser>,
    visibleOngoingEvents: List<OngoingEvent>,
    tick: Long
) : GameUserData(gameUser, otherGameUsers, visibleOngoingEvents, tick)