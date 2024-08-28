package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.jsonparser.banvote

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.jsonparser.NettyRequestJsonParser
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.banvote.VoteSpotCastRequestMessage
import org.springframework.stereotype.Component

@Component
class VoteSpotCastRequestJsonParser : NettyRequestJsonParser {
    override fun getDecodeClass(): Class<out NettyRequestMessage> =
        VoteSpotCastRequestMessage::class.java
}