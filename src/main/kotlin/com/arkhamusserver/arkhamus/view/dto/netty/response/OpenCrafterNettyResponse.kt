package com.arkhamusserver.arkhamus.view.dto.netty.response

import com.arkhamusserver.arkhamus.model.enums.ingame.CrafterType
import com.arkhamusserver.arkhamus.model.enums.ingame.MapObjectState
import com.arkhamusserver.arkhamus.model.redis.RedisContainer

class OpenCrafterNettyResponse(
    var itemsInside: List<InventoryCell> = emptyList(),
    var state: MapObjectState,
    var crafterType: CrafterType,
    var holdingUser: Long?,
    tick: Long,
    userId: Long,
    myGameUser: MyGameUserResponseMessage,
    otherGameUsers: List<NettyGameUserResponseMessage>,
    ongoingEvents: List<OngoingEventResponse>,
    ongoingCraftingProcess: List<CraftProcessResponse>,
    availableAbilities: List<AbilityOfUserResponse>,
    userInventory: List<InventoryCell>,
    containers: List<RedisContainer>
) : NettyResponseMessage(
    tick = tick,
    userId = userId,
    myGameUser = myGameUser,
    otherGameUsers = otherGameUsers,
    ongoingEvents = ongoingEvents,
    ongoingCraftingProcess = ongoingCraftingProcess,
    availableAbilities = availableAbilities,
    userInventory = userInventory,
    containers = containers.convertToContainerInfo(),
    type = OpenCrafterNettyResponse::class.java.simpleName
)