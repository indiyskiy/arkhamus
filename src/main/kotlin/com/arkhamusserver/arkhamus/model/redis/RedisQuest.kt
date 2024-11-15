package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RedisGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.QuestDifficulty
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithTrueIngameId

data class RedisQuest(
    override var id: String,
    override var gameId: Long,
    var questId: Long,
    var startQuestGiverId: Long,
    var endQuestGiverId: Long,
    var difficulty: QuestDifficulty,
    var levelTaskIds: MutableList<Long> = mutableListOf(),
    var textKey: String,
) : RedisGameEntity, WithTrueIngameId {
    override fun inGameId(): Long {
        return questId
    }
}