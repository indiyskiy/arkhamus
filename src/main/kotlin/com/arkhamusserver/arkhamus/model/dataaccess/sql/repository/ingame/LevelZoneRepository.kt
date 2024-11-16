package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame

import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.LevelZone
import org.springframework.data.repository.CrudRepository

interface LevelZoneRepository : CrudRepository<LevelZone, Long> {
    fun findByLevelId(levelId: Long): List<LevelZone>
}