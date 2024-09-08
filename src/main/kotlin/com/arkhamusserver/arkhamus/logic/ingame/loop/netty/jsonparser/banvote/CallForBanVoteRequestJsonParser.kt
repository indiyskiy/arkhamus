package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.jsonparser.banvote

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.jsonparser.NettyRequestJsonParser
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.banvote.CallForBanVoteRequestMessage
import org.springframework.stereotype.Component

@Component
class CallForBanVoteRequestJsonParser : NettyRequestJsonParser {
    override fun getDecodeClass(): Class<out NettyRequestMessage> =
        CallForBanVoteRequestMessage::class.java
}