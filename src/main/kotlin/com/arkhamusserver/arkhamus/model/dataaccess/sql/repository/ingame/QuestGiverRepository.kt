package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame

import com.arkhamusserver.arkhamus.model.database.entity.game.QuestGiver
import org.springframework.data.repository.CrudRepository

interface QuestGiverRepository : CrudRepository<QuestGiver, Long> {
    fun findByLevelId(levelId: Long): List<QuestGiver>
}