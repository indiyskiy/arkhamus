package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository

import com.arkhamusserver.arkhamus.model.database.entity.Level
import com.arkhamusserver.arkhamus.model.enums.LevelState
import org.springframework.data.repository.CrudRepository


interface LevelRepository : CrudRepository<Level, Long> {
    fun findByLevelId(levelId: Long): List<Level>
    fun findByState(state: LevelState): List<Level>
}