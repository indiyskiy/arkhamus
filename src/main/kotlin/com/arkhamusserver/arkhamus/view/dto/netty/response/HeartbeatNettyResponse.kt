package com.arkhamusserver.arkhamus.view.dto.netty.response

class HeartbeatNettyResponse(
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
    HeartbeatNettyResponse::class.java.simpleName
)