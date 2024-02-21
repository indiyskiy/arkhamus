package com.arkhamusserver.arkhamus.view.dto.netty.response

open class NettyResponseMessage(
    val tick: Long,
    val userId: Long,
    val myGameUser: MyGameUserResponseMessage,
    val otherGameUsers: List<NettyGameUserResponseMessage>,
    val type: String
)