package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame

import com.arkhamusserver.arkhamus.model.database.entity.game.Quest
import com.arkhamusserver.arkhamus.model.enums.ingame.QuestState
import org.springframework.data.repository.CrudRepository

interface QuestRepository : CrudRepository<Quest, Long> {
    fun findByLevelId(levelId: Long): List<Quest>
    fun findByLevelIdAndQuestState(levelId: Long, questState: QuestState): List<Quest>
    fun findByQuestState(questState: QuestState): List<Quest>
}