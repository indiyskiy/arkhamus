package com.arkhamusserver.arkhamus.view.dto.netty.response

class ContainerNettyResponse(
    var containerCells: List<NettyContainerCell> = emptyList(),
    tick: Long,
    userId: Long,
    myGameUser: MyGameUserResponseMessage,
    otherGameUsers: List<NettyGameUserResponseMessage>,
    ongoingEffects: List<OngoingEventResponse>,
) : NettyResponseMessage(
    tick,
    userId,
    myGameUser,
    otherGameUsers,
    ongoingEffects,
    ContainerNettyResponse::class.java.simpleName
)