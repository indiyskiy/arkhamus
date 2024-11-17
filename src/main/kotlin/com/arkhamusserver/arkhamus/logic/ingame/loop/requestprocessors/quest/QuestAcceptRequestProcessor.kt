package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.quest

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest.QuestProgressHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.quest.QuestAcceptRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.NettyRequestProcessor
import org.springframework.stereotype.Component

@Component
class QuestAcceptRequestProcessor(
    private val questProgressHandler: QuestProgressHandler,
) : NettyRequestProcessor {
    override fun accept(request: NettyTickRequestMessageDataHolder): Boolean {
        return request.requestProcessData is QuestAcceptRequestProcessData
    }

    override fun process(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        val questAcceptRequestProcessData = requestDataHolder.requestProcessData as QuestAcceptRequestProcessData
        if (questAcceptRequestProcessData.canAccept) {
            questProgressHandler.acceptTheQuest(
                globalGameData.game,
                questAcceptRequestProcessData.userQuestProgress,
                questAcceptRequestProcessData,
                globalGameData.game.globalTimer
            )
        }
    }
}