package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.jsonparser.banvote

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.jsonparser.NettyRequestJsonParser
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.banvote.VoteSpotOpenRequestMessage
import org.springframework.stereotype.Component

@Component
class VoteSpotOpenRequestJsonParser : NettyRequestJsonParser {
    override fun getDecodeClass(): Class<out NettyRequestMessage> =
        VoteSpotOpenRequestMessage::class.java
}