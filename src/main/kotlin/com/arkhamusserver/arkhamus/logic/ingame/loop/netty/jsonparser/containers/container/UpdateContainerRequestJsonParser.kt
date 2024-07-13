package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.jsonparser.containers.container

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.jsonparser.NettyRequestJsonParser
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.containers.container.UpdateContainerRequestMessage
import org.springframework.stereotype.Component

@Component
class UpdateContainerRequestJsonParser : NettyRequestJsonParser {
    override fun getDecodeClass(): Class<out NettyRequestMessage> =
        UpdateContainerRequestMessage::class.java
}