package com.arkhamusserver.arkhamus.view.dto.netty.response

import com.arkhamusserver.arkhamus.model.enums.ingame.Ability

class AbilityNettyResponse(
    private val ability: Ability,
    private val castedSuccessfully: Boolean,
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
    AbilityNettyResponse::class.java.simpleName
)