package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.geometry

import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Tetragon
import org.springframework.data.repository.CrudRepository

interface TetragonRepository : CrudRepository<Tetragon, Long> {
    fun findByLevelZoneId(levelZoneId: Long): List<Tetragon>
    fun findByLevelZoneLevelId(levelId: Long): List<Tetragon>
}