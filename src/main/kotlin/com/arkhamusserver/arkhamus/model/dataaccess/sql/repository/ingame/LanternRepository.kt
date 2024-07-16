package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame

import com.arkhamusserver.arkhamus.model.database.entity.game.Lantern
import org.springframework.data.repository.CrudRepository


interface LanternRepository : CrudRepository<Lantern, Long> {
    fun findByLevelId(levelId: Long): List<Lantern>
}