package com.arkhamusserver.arkhamus.view.dto.netty.response

class NettyGameStartedResponse(
    val message: String = "STARTED",
    tick: Long = 0,
    userId: Long,
    myGameUser: MyGameUserResponseMessage,
    allGameUsers: List<NettyGameUserResponseMessage>,
) : NettyResponseMessage(
    tick = tick,
    userId = userId,
    myGameUser = myGameUser,
    otherGameUsers = allGameUsers,
    ongoingCraftingProcess = emptyList(),
    ongoingEvents = emptyList(),
    availableAbilities = emptyList(),
    userInventory = emptyList(),
    type = NettyGameStartedResponse::class.java.simpleName
)