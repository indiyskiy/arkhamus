package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame

import com.arkhamusserver.arkhamus.model.database.entity.game.Quest
import org.springframework.data.repository.CrudRepository

interface QuestRepository : CrudRepository<Quest, Long> {
    fun findByLevelId(levelId: Long): List<Quest>
}