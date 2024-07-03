package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame

import com.arkhamusserver.arkhamus.model.database.entity.Ellipse
import com.arkhamusserver.arkhamus.model.database.entity.LevelZone
import org.springframework.data.repository.CrudRepository

interface EllipseRepository : CrudRepository<Ellipse, Long> {
    fun findByLevelZone(levelZone: LevelZone): List<Ellipse>
    fun findByLevelZoneId(levelZoneId: Long): List<Ellipse>
}