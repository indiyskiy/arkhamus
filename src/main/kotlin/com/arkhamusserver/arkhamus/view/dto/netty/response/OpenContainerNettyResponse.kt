package com.arkhamusserver.arkhamus.view.dto.netty.response

import com.arkhamusserver.arkhamus.model.enums.ingame.MapObjectState

class OpenContainerNettyResponse(
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
    OpenContainerNettyResponse::class.java.simpleName
)