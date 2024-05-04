package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.jsonparser

import com.arkhamusserver.arkhamus.view.dto.netty.request.CloseCrafterRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyRequestMessage
import org.springframework.stereotype.Component

@Component
class CloseCrafterRequestJsonParser : NettyRequestJsonParser {
    override fun getDecodeClass(): Class<out NettyRequestMessage> =
        CloseCrafterRequestMessage::class.java
}