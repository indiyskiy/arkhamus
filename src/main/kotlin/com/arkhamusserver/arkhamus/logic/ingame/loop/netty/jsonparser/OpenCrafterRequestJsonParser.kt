package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.jsonparser

import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.OpenCrafterRequestMessage
import org.springframework.stereotype.Component

@Component
class OpenCrafterRequestJsonParser : NettyRequestJsonParser {
    override fun getDecodeClass(): Class<out NettyRequestMessage> =
        OpenCrafterRequestMessage::class.java
}