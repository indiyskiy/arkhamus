package com.arkhamusserver.arkhamus.logic.ingame.loop.netty

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.ArkhamusChannel
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyTickRequestMessage

data class NettyTickRequestMessageContainer(
    val nettyRequestMessage: NettyTickRequestMessage,
    val arkhamusChannel: ArkhamusChannel,
    val registrationTime: Long = System.currentTimeMillis()
)