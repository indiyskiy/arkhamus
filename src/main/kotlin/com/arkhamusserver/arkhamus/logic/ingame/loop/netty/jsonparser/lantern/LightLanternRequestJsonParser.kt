package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.jsonparser.lantern

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.jsonparser.NettyRequestJsonParser
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.lantern.LightLanternRequestMessage
import org.springframework.stereotype.Component

@Component
class LightLanternRequestJsonParser : NettyRequestJsonParser {
    override fun getDecodeClass(): Class<out NettyRequestMessage> =
        LightLanternRequestMessage::class.java
}