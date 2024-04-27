package com.arkhamusserver.arkhamus.view.dto.netty.response

class HeartbeatNettyResponse(
    tick: Long,
    userId: Long,
    myGameUser: MyGameUserResponseMessage,
    otherGameUsers: List<NettyGameUserResponseMessage>,
    ongoingEvents: List<OngoingEventResponse>,
    availableAbilities: List<AbilityOfUserResponse>
) : NettyResponseMessage(
    tick,
    userId,
    myGameUser,
    otherGameUsers,
    ongoingEvents,
    availableAbilities,
    HeartbeatNettyResponse::class.java.simpleName
)