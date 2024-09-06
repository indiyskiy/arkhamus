package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame

import com.arkhamusserver.arkhamus.model.database.entity.game.Threshold
import org.springframework.data.repository.CrudRepository

interface ThresholdRepository : CrudRepository<Threshold, Long> {
    fun findByLevelId(levelId: Long): List<Threshold>
}