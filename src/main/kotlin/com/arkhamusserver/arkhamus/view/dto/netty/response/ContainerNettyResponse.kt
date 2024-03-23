package com.arkhamusserver.arkhamus.view.dto.netty.response

import com.arkhamusserver.arkhamus.model.enums.ingame.MapObjectState

class ContainerNettyResponse(
    var containerCells: List<ContainerCell> = emptyList(),
    var containerState: MapObjectState,
    var holdingUser: Long?,
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
    ContainerNettyResponse::class.java.simpleName
)