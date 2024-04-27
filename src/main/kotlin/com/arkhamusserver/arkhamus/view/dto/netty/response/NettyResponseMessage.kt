package com.arkhamusserver.arkhamus.view.dto.netty.response

 abstract class NettyResponseMessage(
    val tick: Long,
    val userId: Long,
    val myGameUser: MyGameUserResponseMessage,
    val otherGameUsers: List<NettyGameUserResponseMessage>,
    val ongoingEvents: List<OngoingEventResponse>,
    val availableAbilities: List<AbilityOfUserResponse>,
    val type: String
)