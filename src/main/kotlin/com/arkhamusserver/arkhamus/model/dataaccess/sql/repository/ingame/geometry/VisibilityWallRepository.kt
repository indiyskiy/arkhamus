package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.geometry

import com.arkhamusserver.arkhamus.model.database.entity.game.VisibilityWall
import org.springframework.data.repository.CrudRepository

interface VisibilityWallRepository : CrudRepository<VisibilityWall, Long> {
    fun findByLevelId(levelId: Long): List<VisibilityWall>
}