package com.arkhamusserver.arkhamus.view.dto.netty.response

import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.CraftProcessResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.AbilityOfUserResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.ContainerState
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.InventoryCell

abstract class NettyResponse(
    val tick: Long,
    val userId: Long,
    val myGameUser: MyGameUserResponse,
    val otherGameUsers: List<NettyGameUserResponse>,
    val ongoingEvents: List<OngoingEventResponse>,
    val ongoingCraftingProcess: List<CraftProcessResponse>,
    val availableAbilities: List<AbilityOfUserResponse>,
    var userInventory: List<InventoryCell>,
    var containers: List<ContainerState>,
    val type: String
)

fun List<RedisContainer>.convertToContainerInfo(): List<ContainerState> {
    return this.map { container ->
        ContainerState(
            containerId = container.containerId,
            state = container.state,
            holdingUserId = container.holdingUser
        )
    }
}