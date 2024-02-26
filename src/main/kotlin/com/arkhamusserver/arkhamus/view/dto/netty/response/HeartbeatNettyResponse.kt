package com.arkhamusserver.arkhamus.view.dto.netty.response

class HeartbeatNettyResponse(
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
    HeartbeatNettyResponse::class.java.simpleName
)