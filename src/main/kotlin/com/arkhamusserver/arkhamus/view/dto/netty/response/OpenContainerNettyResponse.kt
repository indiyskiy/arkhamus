package com.arkhamusserver.arkhamus.view.dto.netty.response

import com.arkhamusserver.arkhamus.model.enums.ingame.MapObjectState
import com.arkhamusserver.arkhamus.model.redis.RedisContainer

class OpenContainerNettyResponse(
    var itemsInside: List<InventoryCell> = emptyList(),
    var state: MapObjectState,
    var holdingUser: Long?,
    tick: Long,
    userId: Long,
    myGameUser: MyGameUserResponseMessage,
    otherGameUsers: List<NettyGameUserResponseMessage>,
    ongoingEvents: List<OngoingEventResponse>,
    availableAbilities: List<AbilityOfUserResponse>,
    ongoingCraftingProcess: List<CraftProcessResponse>,
    userInventory: List<InventoryCell>,
    containers: List<RedisContainer>
) : NettyResponseMessage(
    tick = tick,
    userId = userId,
    myGameUser = myGameUser,
    otherGameUsers = otherGameUsers,
    ongoingEvents = ongoingEvents,
    availableAbilities = availableAbilities,
    ongoingCraftingProcess = ongoingCraftingProcess,
    userInventory = userInventory,
    containers = containers.convertToContainerInfo(),
    type = OpenContainerNettyResponse::class.java.simpleName
)