package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.jsonparser.ritual

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.jsonparser.NettyRequestJsonParser
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.ritual.GodVoteCastRequestMessage
import org.springframework.stereotype.Component

@Component
class GodVoteCastRequestJsonParser : NettyRequestJsonParser {
    override fun getDecodeClass(): Class<out NettyRequestMessage> =
        GodVoteCastRequestMessage::class.java
}