package com.arkhamusserver.arkhamus.view.dto.netty.response

class AbilityNettyResponse(
    private val abilityId: Int?,
    private val castedSuccessfully: Boolean,
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
    AbilityNettyResponse::class.java.simpleName
)