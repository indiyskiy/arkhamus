package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository

import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import org.springframework.data.repository.CrudRepository
import java.util.*


interface UserOfGameSessionRepository : CrudRepository<UserOfGameSession, Long> {
    override fun findById(id: Long): Optional<UserOfGameSession>

    fun findByUserAccountId(userAccountId: Long): List<UserOfGameSession>

    fun findByUserAccountIdAndLeft(userAccountId: Long, left: Boolean = false): List<UserOfGameSession>

    fun findByGameSessionId(gameSessionId: Long): List<UserOfGameSession>

    fun findByGameSessionIdAndLeft(gameSessionId: Long, left: Boolean = false): List<UserOfGameSession>
}