package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.jsonparser

import com.arkhamusserver.arkhamus.view.dto.netty.request.HeartbeatRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyRequestMessage
import org.springframework.stereotype.Component

@Component
class NettyHeartbeatRequestJsonParser : NettyRequestJsonParser {
    override fun getDecodeClass(): Class<out NettyRequestMessage> =
        HeartbeatRequestMessage::class.java
}