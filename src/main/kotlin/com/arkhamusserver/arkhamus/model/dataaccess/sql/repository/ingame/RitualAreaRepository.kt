package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame

import com.arkhamusserver.arkhamus.model.database.entity.game.RitualArea
import org.springframework.data.repository.CrudRepository

interface RitualAreaRepository : CrudRepository<RitualArea, Long> {
    fun findByLevelId(levelId: Long): List<RitualArea>
}