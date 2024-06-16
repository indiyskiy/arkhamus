package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.jsonparser.ritual

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.jsonparser.NettyRequestJsonParser
import com.arkhamusserver.arkhamus.view.dto.netty.request.ritual.GodVoteSkipRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyRequestMessage
import org.springframework.stereotype.Component

@Component
class GodVoteSkipRequestJsonParser : NettyRequestJsonParser {
    override fun getDecodeClass(): Class<out NettyRequestMessage> =
        GodVoteSkipRequestMessage::class.java
}