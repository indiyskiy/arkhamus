package com.arkhamusserver.arkhamus.view.dto.netty.response

data class NettyResponseMessageContainer (
    val nettyResponseMessage: NettyResponseMessage,
    val userId: Long,
    val channelId: String
)