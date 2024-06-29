package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.jsonparser

import com.arkhamusserver.arkhamus.view.dto.netty.request.ISawTheEndOfTimesRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyRequestMessage
import org.springframework.stereotype.Component

@Component
class ISawTheEndOfTimesRequestJsonParser : NettyRequestJsonParser {
    override fun getDecodeClass(): Class<out NettyRequestMessage> =
        ISawTheEndOfTimesRequestMessage::class.java
}