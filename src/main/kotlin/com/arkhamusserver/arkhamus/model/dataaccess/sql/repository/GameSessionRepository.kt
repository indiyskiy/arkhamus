package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository

import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.enums.GameState
import org.springframework.data.repository.CrudRepository
import java.util.*

interface GameSessionRepository : CrudRepository<GameSession, Long> {
    override fun findById(id: Long): Optional<GameSession>
    fun findByToken(token: String): List<GameSession>
    fun findByState(state: GameState): List<GameSession>
    fun findByGameSessionSettingsLevelId(levelId: Long): List<GameSession>
}