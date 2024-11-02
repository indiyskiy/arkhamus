package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType.*
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithStringId
import org.springframework.stereotype.Component

@Component
class GameObjectFinder {

    fun findById(
        id: String,
        type: GameObjectType,
        data: GlobalGameData
    ): WithStringId? {
        return when (type) {
            CHARACTER -> data.users[id.toLong()]
            VOTE_SPOT -> data.voteSpots.firstOrNull { it.inGameId() == id.toLong() }
            CONTAINER -> data.containers[id.toLong()]
            CRAFTER -> data.crafters[id.toLong()]
            CLUE -> data.clues.firstOrNull { it.id == id }
            ALTAR -> data.altars[id.toLong()]
            QUEST_GIVER -> data.questGivers.firstOrNull { it.inGameId() == id.toLong() }
            else -> null

        }
    }

    fun all(
        types: List<GameObjectType>,
        data: GlobalGameData
    ): List<WithStringId> {
        return types.flatMap { type ->
            when (type) {
                CHARACTER -> data.users.values
                VOTE_SPOT -> data.voteSpots
                CONTAINER -> data.containers.values
                CRAFTER -> data.crafters.values
                CLUE -> data.clues
                ALTAR -> data.altars.values
                QUEST_GIVER -> data.questGivers
            }
        }
    }
}