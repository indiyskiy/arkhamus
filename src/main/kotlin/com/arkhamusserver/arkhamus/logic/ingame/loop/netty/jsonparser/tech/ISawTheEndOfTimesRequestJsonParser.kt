package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.jsonparser.tech

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.jsonparser.NettyRequestJsonParser
import com.arkhamusserver.arkhamus.view.dto.netty.request.tech.ISawTheEndOfTimesRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyRequestMessage
import org.springframework.stereotype.Component

@Component
class ISawTheEndOfTimesRequestJsonParser : NettyRequestJsonParser {
    override fun getDecodeClass(): Class<out NettyRequestMessage> =
        ISawTheEndOfTimesRequestMessage::class.java
}