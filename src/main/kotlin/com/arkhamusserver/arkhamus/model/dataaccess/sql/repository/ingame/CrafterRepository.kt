package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame

import com.arkhamusserver.arkhamus.model.database.entity.Crafter
import org.springframework.data.repository.CrudRepository
import java.util.*

interface CrafterRepository : CrudRepository<Crafter, Long> {
    override fun findById(id: Long): Optional<Crafter>
    fun findByLevelId(levelId: Long): List<Crafter>
}