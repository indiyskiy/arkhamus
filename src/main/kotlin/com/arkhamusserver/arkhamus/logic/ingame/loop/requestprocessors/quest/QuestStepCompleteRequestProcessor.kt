package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.quest

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest.QuestProgressHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.quest.QuestStepCompleteRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.NettyRequestProcessor
import org.springframework.stereotype.Component

@Component
class QuestStepCompleteRequestProcessor(
    private val questProgressHandler: QuestProgressHandler,
) : NettyRequestProcessor {
    override fun accept(request: NettyTickRequestMessageDataHolder): Boolean {
        return request.requestProcessData is QuestStepCompleteRequestProcessData
    }

    override fun process(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        val questStepCompleteRequestProcessData =
            requestDataHolder.requestProcessData as QuestStepCompleteRequestProcessData
        val quest = questStepCompleteRequestProcessData.quest
        val userQuestProgress = questStepCompleteRequestProcessData.userQuestProgress

        questProgressHandler.nextStep(userQuestProgress, quest)
        if (questProgressHandler.isCompleted(quest, userQuestProgress)) {
            questStepCompleteRequestProcessData.canDecline = false
            questStepCompleteRequestProcessData.canFinish = true
        }
    }
}