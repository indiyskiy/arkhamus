package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.AbilityOfUserResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.InventoryCell
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.CraftProcessResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.GameUserResponse

open class GameUserData(
    val gameUser: RedisGameUser?,
    val otherGameUsers: List<RedisGameUser>,
    val inZones: List<LevelZone>,
    val visibleOngoingEvents: List<OngoingEvent>,
    val availableAbilities: List<AbilityOfUserResponse>,
    var ongoingCraftingProcess: List<CraftProcessResponse>,
    var visibleItems: List<InventoryCell>,
    var containers: List<RedisContainer>,
    tick: Long
) : RequestProcessData(
    tick = tick
)

fun GameUserData.otherGameUsersResponseMessage() = otherGameUsers.map {
    GameUserResponse(it)
}