package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.jsonparser.containers.crafter

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.jsonparser.NettyRequestJsonParser
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.containers.crafter.CraftProcessRequestMessage
import org.springframework.stereotype.Component

@Component
class CraftProcessRequestJsonParser : NettyRequestJsonParser {
    override fun getDecodeClass(): Class<out NettyRequestMessage> =
        CraftProcessRequestMessage::class.java
}