package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.jsonparser.quest

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.jsonparser.NettyRequestJsonParser
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.quest.LevelTaskCompleteRequestMessage
import org.springframework.stereotype.Component

@Component
class LevelTaskCompleteRequestJsonParser : NettyRequestJsonParser {
    override fun getDecodeClass(): Class<out NettyRequestMessage> =
        LevelTaskCompleteRequestMessage::class.java
}