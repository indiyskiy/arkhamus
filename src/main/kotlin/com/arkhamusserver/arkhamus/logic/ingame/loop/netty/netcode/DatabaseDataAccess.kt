package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserOfGameSessionRepository
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component

@Component
class DatabaseDataAccess(
    private val userOfGameSessionRepository: UserOfGameSessionRepository
) {
    @Transactional
    fun findByUserAccountId(accountId: Long):  List<UserOfGameSession> {
       return userOfGameSessionRepository.findByUserAccountId(accountId)
    }
}