package com.arkhamusserver.arkhamus.view.dto.netty.response

class HeartbeatNettyResponse(
    tick: Long,
    userId: Long,
    myGameUser: MyGameUserResponseMessage,
    otherGameUsers: List<NettyGameUserResponseMessage>
) : NettyResponseMessage(
    tick,
    userId,
    myGameUser,
    otherGameUsers,
    HeartbeatNettyResponse::class.java.simpleName
)