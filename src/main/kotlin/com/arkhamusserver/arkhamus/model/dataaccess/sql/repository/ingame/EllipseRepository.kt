package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame

import com.arkhamusserver.arkhamus.model.database.entity.game.Ellipse
import org.springframework.data.repository.CrudRepository

interface EllipseRepository : CrudRepository<Ellipse, Long> {
    fun findByLevelZoneId(levelZoneId: Long): List<Ellipse>
    fun findByLevelZoneLevelId(levelZoneId: Long): List<Ellipse>
}