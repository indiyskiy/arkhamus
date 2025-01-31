package com.arkhamusserver.arkhamus.model.dataaccess.ingame

import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.RamCrudRepository
import com.arkhamusserver.arkhamus.model.ingame.InGameGameUser
import org.springframework.stereotype.Repository

@Repository
class InGameGameUserRepository : RamCrudRepository<InGameGameUser>() {
    fun findByUserIdAndGameId(userId: Long, gameId: Long): List<InGameGameUser> =
        map.values.filter { it.inGameId() == userId && it.gameId == gameId }

    fun findByUserId(userId: Long): List<InGameGameUser> =
        map.values.filter { it.inGameId() == userId }
}