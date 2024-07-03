package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame

import com.arkhamusserver.arkhamus.model.database.entity.Crafter
import org.springframework.data.repository.CrudRepository

interface CrafterRepository : CrudRepository<Crafter, Long> {
    fun findByLevelId(levelId: Long): List<Crafter>
}