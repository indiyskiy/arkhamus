package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.jsonparser.quest

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.jsonparser.NettyRequestJsonParser
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.quest.QuestAcceptRequestMessage
import org.springframework.stereotype.Component

@Component
class QuestAcceptRequestJsonParser : NettyRequestJsonParser {
    override fun getDecodeClass(): Class<out NettyRequestMessage> =
        QuestAcceptRequestMessage::class.java
}