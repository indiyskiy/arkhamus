package com.arkhamusserver.arkhamus.view.dto.netty.response
class NettyGameStartedResponse(
    val message: String = "STARTED",
    tick: Long = 0,
    userId: Long,
    myGameUser: MyGameUserResponseMessage,
    allGameUsers: List<NettyGameUserResponseMessage>,
) : NettyResponseMessage(
    tick,
    userId,
    myGameUser,
    allGameUsers,
    NettyGameStartedResponse::class.java.simpleName
)