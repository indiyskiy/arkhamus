package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.jsonparser.containers.crafter

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.jsonparser.NettyRequestJsonParser
import com.arkhamusserver.arkhamus.view.dto.netty.request.containers.crafter.UpdateCrafterRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyRequestMessage
import org.springframework.stereotype.Component

@Component
class UpdateCrafterRequestJsonParser : NettyRequestJsonParser {
    override fun getDecodeClass(): Class<out NettyRequestMessage> =
        UpdateCrafterRequestMessage::class.java
}