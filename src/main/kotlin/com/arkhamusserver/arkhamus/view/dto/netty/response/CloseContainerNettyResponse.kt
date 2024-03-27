package com.arkhamusserver.arkhamus.view.dto.netty.response

class CloseContainerNettyResponse(
    var userInventory: List<ContainerCell> = emptyList(),
    tick: Long,
    userId: Long,
    myGameUser: MyGameUserResponseMessage,
    otherGameUsers: List<NettyGameUserResponseMessage>,
    ongoingEvents: List<OngoingEventResponse>,
) : NettyResponseMessage(
    tick,
    userId,
    myGameUser,
    otherGameUsers,
    ongoingEvents,
    CloseContainerNettyResponse::class.java.simpleName
)