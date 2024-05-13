package com.arkhamusserver.arkhamus.view.dto.netty.response

class CraftProcessNettyResponse(
    var recipeId: Int?,
    var crafterId: Long?,
    var startedSuccessfully: Boolean,
    var sortedUserInventory: List<InventoryCell>,
    var itemsInside: List<InventoryCell>,
    tick: Long,
    userId: Long,
    myGameUser: MyGameUserResponseMessage,
    otherGameUsers: List<NettyGameUserResponseMessage>,
    ongoingEvents: List<OngoingEventResponse>,
    availableAbilities: List<AbilityOfUserResponse>,
    ongoingCraftingProcess: List<CraftProcessResponse>,
    userInventory: List<InventoryCell>,
) : NettyResponseMessage(
    tick = tick,
    userId = userId,
    myGameUser = myGameUser,
    otherGameUsers = otherGameUsers,
    ongoingEvents = ongoingEvents,
    availableAbilities = availableAbilities,
    ongoingCraftingProcess = ongoingCraftingProcess,
    userInventory = userInventory,
    type = CraftProcessNettyResponse::class.java.simpleName
)