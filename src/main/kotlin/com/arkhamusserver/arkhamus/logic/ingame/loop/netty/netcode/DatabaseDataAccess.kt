package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.GameSessionRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserOfGameSessionRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Component

@Component
class DatabaseDataAccess(
    private val userOfGameSessionRepository: UserOfGameSessionRepository,
    private val gameSessionRepository: GameSessionRepository
) {
    @Transactional
    fun findByUserAccountId(accountId: Long): List<UserOfGameSession> {
        return userOfGameSessionRepository.findByUserAccountId(accountId)
    }

    @Transactional
    fun findByGameId(gameId: Long): GameSession {
        return gameSessionRepository.findById(gameId).get()
    }
}