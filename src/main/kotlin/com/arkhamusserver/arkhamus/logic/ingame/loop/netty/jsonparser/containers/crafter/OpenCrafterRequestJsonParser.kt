package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.jsonparser.containers.crafter

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.jsonparser.NettyRequestJsonParser
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.containers.crafter.OpenCrafterRequestMessage
import org.springframework.stereotype.Component

@Component
class OpenCrafterRequestJsonParser : NettyRequestJsonParser {
    override fun getDecodeClass(): Class<out NettyRequestMessage> =
        OpenCrafterRequestMessage::class.java
}