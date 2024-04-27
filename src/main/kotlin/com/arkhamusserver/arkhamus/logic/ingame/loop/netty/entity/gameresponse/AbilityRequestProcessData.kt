package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.model.enums.ingame.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.AbilityOfUserResponse

class AbilityRequestProcessData(
    val ability: Ability?,
    val canBeCasted: Boolean,
    val cooldown: Long?,
    val cooldownOf: Long?,
    var castedSuccessfully: Boolean,
    val item: Item?,
    gameUser: RedisGameUser,
    otherGameUsers: List<RedisGameUser>,
    visibleOngoingEvents: List<OngoingEvent>,
    availableAbilities: List<AbilityOfUserResponse>,
    tick: Long
) : GameUserData(gameUser, otherGameUsers, visibleOngoingEvents, availableAbilities, tick)