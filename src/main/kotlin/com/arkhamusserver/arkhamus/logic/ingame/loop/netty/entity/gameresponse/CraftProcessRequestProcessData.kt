package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse

import com.arkhamusserver.arkhamus.logic.ingame.item.Recipe
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.model.redis.RedisCrafter
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.AbilityOfUserResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.ContainerCell

class CraftProcessRequestProcessData(
    val recipe: Recipe?,
    val crafter: RedisCrafter?,
    val canBeStarted: Boolean,
    var startedSuccessfully: Boolean,
    gameUser: RedisGameUser,
    otherGameUsers: List<RedisGameUser>,
    visibleOngoingEvents: List<OngoingEvent>,
    availableAbilities: List<AbilityOfUserResponse>,
    visibleItems: List<ContainerCell>,
    tick: Long
) : GameUserData(
    gameUser,
    otherGameUsers,
    visibleOngoingEvents,
    availableAbilities,
    visibleItems,
    tick
)