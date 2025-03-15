package com.arkhamusserver.arkhamus.model.ingame

import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.InGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.QuestDifficulty
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithTrueIngameId

data class InGameQuest(
    override var id: String,
    override var gameId: Long,
    var questId: Long,
    var startQuestGiverId: Long,
    var endQuestGiverId: Long,
    var difficulty: QuestDifficulty,
    var levelTasks: List<InGameTask> = emptyList(),
    var textKey: String,
) : InGameEntity, WithTrueIngameId {
    override fun inGameId(): Long {
        return questId
    }
}