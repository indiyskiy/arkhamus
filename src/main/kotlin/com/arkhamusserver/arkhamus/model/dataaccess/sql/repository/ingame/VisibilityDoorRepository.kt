package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame

import com.arkhamusserver.arkhamus.model.database.entity.game.VisibilityDoor
import org.springframework.data.repository.CrudRepository

interface VisibilityDoorRepository : CrudRepository<VisibilityDoor, Long> {
    fun findByLevelId(levelId: Long): List<VisibilityDoor>
}