package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository

import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import org.springframework.data.repository.CrudRepository
import java.util.*

interface GameRepository : CrudRepository<GameSession, Long> {
    override fun findById(id: Long): Optional<GameSession>
}