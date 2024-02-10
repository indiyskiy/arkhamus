package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository

import com.arkhamusserver.arkhamus.model.database.entity.StartMarker
import org.springframework.data.repository.CrudRepository


interface StartMarkerRepository : CrudRepository<StartMarker, Long> {
    fun findByLevelId(levelId: Long): List<StartMarker>
}