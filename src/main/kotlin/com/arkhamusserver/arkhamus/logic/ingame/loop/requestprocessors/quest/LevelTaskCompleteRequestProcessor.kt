package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.quest

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest.QuestProgressHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.quest.LevelTaskCompleteRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.NettyRequestProcessor
import org.springframework.stereotype.Component

@Component
class LevelTaskCompleteRequestProcessor(
    private val questProgressHandler: QuestProgressHandler,
) : NettyRequestProcessor {
    override fun accept(request: NettyTickRequestMessageDataHolder): Boolean {
        return request.requestProcessData is LevelTaskCompleteRequestProcessData
    }

    override fun process(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        val levelTaskCompleteRequestProcessData =
            requestDataHolder.requestProcessData as LevelTaskCompleteRequestProcessData
        val quest = levelTaskCompleteRequestProcessData.quest
        val userQuestProgress = levelTaskCompleteRequestProcessData.userQuestProgress
        val user = globalGameData.users[userQuestProgress?.userId]

        questProgressHandler.nextStep(
            userQuestProgress,
            quest,
            globalGameData,
            user
        )
        if (questProgressHandler.isCompleted(quest, userQuestProgress)) {
            levelTaskCompleteRequestProcessData.canDecline = false
            levelTaskCompleteRequestProcessData.canFinish = true
        }
    }
}