package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType.*
import org.springframework.stereotype.Component

@Component
class GameObjectFinder {

    fun findById(
        id: String,
        type: GameObjectType,
        data: GlobalGameData
    ): Any? {
        return when (type) {
            CHARACTER -> data.users[id.toLong()]
            VOTE_SPOT -> data.voteSpots.firstOrNull { it.gameId == id.toLong() }
        }
    }
}