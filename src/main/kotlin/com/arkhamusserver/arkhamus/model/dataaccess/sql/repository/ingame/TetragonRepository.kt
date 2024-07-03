package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame

import com.arkhamusserver.arkhamus.model.database.entity.LevelZone
import com.arkhamusserver.arkhamus.model.database.entity.Tetragon
import org.springframework.data.repository.CrudRepository

interface TetragonRepository : CrudRepository<Tetragon, Long> {
    fun findByLevelZone(levelZone: LevelZone): List<Tetragon>
    fun findByLevelZoneId(levelZoneId: Long): List<Tetragon>
}