package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.AbilityOfUserResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.InventoryCell
import com.arkhamusserver.arkhamus.view.dto.netty.response.CraftProcessResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyGameUserResponseMessage

open class GameUserData(
    val gameUser: RedisGameUser?,
    val otherGameUsers: List<RedisGameUser> = emptyList(),
    val visibleOngoingEvents: List<OngoingEvent>,
    val availableAbilities: List<AbilityOfUserResponse>,
    var ongoingCraftingProcess: List<CraftProcessResponse>,
    var visibleItems: List<InventoryCell>,
    tick: Long
) : RequestProcessData(
    tick = tick
)

fun GameUserData.otherGameUsersResponseMessage() = otherGameUsers.map {
    NettyGameUserResponseMessage(
        id = it.userId,
        nickName = it.nickName,
        x = it.x,
        y = it.y
    )
}