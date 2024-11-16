package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame

import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Door
import org.springframework.data.repository.CrudRepository

interface DoorRepository : CrudRepository<Door, Long> {
    fun findByLevelId(levelId: Long): List<Door>
}