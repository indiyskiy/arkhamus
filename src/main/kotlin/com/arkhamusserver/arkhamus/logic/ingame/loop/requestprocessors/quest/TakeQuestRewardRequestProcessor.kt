package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.quest

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest.QuestProgressHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest.QuestRewardUtils
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.quest.TakeQuestRewardRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.NettyRequestProcessor
import org.springframework.stereotype.Component

@Component
class TakeQuestRewardRequestProcessor(
    private val questProgressHandler: QuestProgressHandler,
    private val questRewardUtils: QuestRewardUtils
) : NettyRequestProcessor {
    override fun accept(request: NettyTickRequestMessageDataHolder): Boolean {
        return request.requestProcessData is TakeQuestRewardRequestProcessData
    }

    override fun process(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        val takeQuestRewardRequestProcessData =
            requestDataHolder.requestProcessData as TakeQuestRewardRequestProcessData
        if (takeQuestRewardRequestProcessData.canFinish) {
            val reward = takeQuestRewardRequestProcessData.questReward
            val quest = takeQuestRewardRequestProcessData.quest
            val user = takeQuestRewardRequestProcessData.gameUser
            val questGiverGivesReward = takeQuestRewardRequestProcessData.questGiverGivesReward
            if (reward != null && quest != null && user != null && questGiverGivesReward != null) {
                questRewardUtils.takeReward(user, reward, globalGameData, questGiverGivesReward)
                questProgressHandler.finishQuest(
                    user,
                    globalGameData,
                    takeQuestRewardRequestProcessData
                )
            }
        }
    }
}