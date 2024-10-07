package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.jsonparser.lantern

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.jsonparser.NettyRequestJsonParser
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.lantern.OpenLanternRequestMessage
import org.springframework.stereotype.Component

@Component
class OpenLanternRequestJsonParser : NettyRequestJsonParser {
    override fun getDecodeClass(): Class<out NettyRequestMessage> =
        OpenLanternRequestMessage::class.java
}