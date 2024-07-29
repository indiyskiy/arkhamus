package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

import com.arkhamusserver.arkhamus.model.enums.ingame.QuestDifficulty

data class QuestInfoResponse(
    var userQuest: UserQuestResponse?,
    var questDifficulty: QuestDifficulty?,
    var rewards: List<QuestRewardResponse>,
    var canAccept: Boolean,
    var canDecline: Boolean,
    var canFinish: Boolean,
)