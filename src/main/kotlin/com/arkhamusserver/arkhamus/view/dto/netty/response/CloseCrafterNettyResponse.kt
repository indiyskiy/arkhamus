package com.arkhamusserver.arkhamus.view.dto.netty.response

class CloseCrafterNettyResponse(
    var sortedUserInventory: List<ContainerCell>,
    tick: Long,
    userId: Long,
    myGameUser: MyGameUserResponseMessage,
    otherGameUsers: List<NettyGameUserResponseMessage>,
    ongoingEvents: List<OngoingEventResponse>,
    availableAbilities: List<AbilityOfUserResponse>,
    userInventory: List<ContainerCell>,
) : NettyResponseMessage(
    tick = tick,
    userId = userId,
    myGameUser = myGameUser,
    otherGameUsers = otherGameUsers,
    ongoingEvents = ongoingEvents,
    availableAbilities = availableAbilities,
    userInventory = userInventory,
    type = CloseCrafterNettyResponse::class.java.simpleName
)