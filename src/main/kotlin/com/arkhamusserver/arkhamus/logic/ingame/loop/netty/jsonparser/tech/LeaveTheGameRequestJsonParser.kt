package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.jsonparser.tech

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.jsonparser.NettyRequestJsonParser
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.tech.LeaveTheGameRequestMessage
import org.springframework.stereotype.Component

@Component
class LeaveTheGameRequestJsonParser : NettyRequestJsonParser {
    override fun getDecodeClass(): Class<out NettyRequestMessage> =
        LeaveTheGameRequestMessage::class.java
}