package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame

import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.LevelTask
import org.springframework.data.repository.CrudRepository

interface LevelTaskRepository : CrudRepository<LevelTask, Long> {
    fun findByLevelId(levelId: Long): List<LevelTask>
}