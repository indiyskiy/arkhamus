package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.jsonparser.lantern

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.jsonparser.NettyRequestJsonParser
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.lantern.FillLanternRequestMessage
import org.springframework.stereotype.Component

@Component
class FillLanternRequestJsonParser : NettyRequestJsonParser {
    override fun getDecodeClass(): Class<out NettyRequestMessage> =
        FillLanternRequestMessage::class.java
}