package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.clues

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.CluesContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Clue
import com.arkhamusserver.arkhamus.model.enums.ingame.core.God
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisLevelZone
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithStringId
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.ExtendedClueResponse

interface AdvancedClueHandler {
    fun addClues(
        session: GameSession,
        god: God,
        zones: List<RedisLevelZone>,
        activeCluesOnStart: Int
    )

    fun mapActualClues(
        container: CluesContainer,
        user: RedisGameUser,
        data: GlobalGameData,
    ): List<ExtendedClueResponse>

    fun mapPossibleClues(
        container: CluesContainer,
        user: RedisGameUser,
        data: GlobalGameData,
    ): List<ExtendedClueResponse>

    fun accept(clues: List<Clue>): Boolean
    fun accept(clue: Clue): Boolean
    fun accept(target: WithStringId): Boolean
    fun canBeAdded(container: CluesContainer): Boolean
    fun addClue(data: GlobalGameData)
    fun canBeRemovedRabdomly(container: CluesContainer): Boolean
    fun canBeRemoved(user: RedisGameUser, target: Any, data: GlobalGameData): Boolean
    fun anyCanBeRemoved(user: RedisGameUser, data: GlobalGameData): Boolean
    fun removeRandom(container: CluesContainer)
    fun removeTarget(
        target: WithStringId,
        data: GlobalGameData
    )
}