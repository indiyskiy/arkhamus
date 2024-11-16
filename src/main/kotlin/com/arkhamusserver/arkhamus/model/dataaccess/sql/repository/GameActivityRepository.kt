package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository

import com.arkhamusserver.arkhamus.model.database.entity.game.GameActivity
import org.springframework.data.repository.CrudRepository
import java.util.*


interface GameActivityRepository : CrudRepository<GameActivity, Long> {

    override fun findById(id: Long): Optional<GameActivity>

    fun findByUserOfGameSessionId(userOfGameSession: Long): List<GameActivity>

    fun findByGameSessionId(gameSessionId: Long): List<GameActivity>

    fun findByGameSessionIdAndUserOfGameSessionId(
        gameSessionId: Long,
        userOfGameSession: Long
    ): List<GameActivity>
}