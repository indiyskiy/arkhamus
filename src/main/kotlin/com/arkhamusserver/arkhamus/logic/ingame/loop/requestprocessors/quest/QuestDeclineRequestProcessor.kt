package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.quest

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest.QuestProgressHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.quest.QuestDeclineRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.NettyRequestProcessor
import org.springframework.stereotype.Component

@Component
class QuestDeclineRequestProcessor(
    private val questProgressHandler: QuestProgressHandler,
) : NettyRequestProcessor {
    override fun accept(request: NettyTickRequestMessageDataHolder): Boolean {
        return request.requestProcessData is QuestDeclineRequestProcessData
    }

    override fun process(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        val questDeclineRequestProcessData = requestDataHolder.requestProcessData as QuestDeclineRequestProcessData
        if (questDeclineRequestProcessData.canDecline) {
            questProgressHandler.declineTheQuest(
                globalGameData.users[requestDataHolder.userAccount.id]!!,
                globalGameData,
                questDeclineRequestProcessData
            )
        }
    }
}