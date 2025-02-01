package com.arkhamusserver.arkhamus.model.dataaccess.ingame

import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.RamCrudRepository
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import org.springframework.stereotype.Repository

@Repository
class InGameGameUserRepository : RamCrudRepository<InGameUser>() {
    fun findByUserIdAndGameId(userId: Long, gameId: Long): List<InGameUser> =
        map.values.filter { it.inGameId() == userId && it.gameId == gameId }

    fun findByUserId(userId: Long): List<InGameUser> =
        map.values.filter { it.inGameId() == userId }
}