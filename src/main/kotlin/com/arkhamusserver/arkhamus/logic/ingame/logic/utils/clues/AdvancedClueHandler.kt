package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.clues

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.CluesContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Clue
import com.arkhamusserver.arkhamus.model.enums.ingame.core.God
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.InGameLevelZone
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithStringId
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.ExtendedClueResponse

interface AdvancedClueHandler {
    fun addClues(
        session: GameSession,
        god: God,
        zones: List<InGameLevelZone>,
        activeCluesOnStart: Int
    )

    fun mapActualClues(
        container: CluesContainer,
        user: InGameUser,
        data: GlobalGameData,
    ): List<ExtendedClueResponse>

    fun mapPossibleClues(
        container: CluesContainer,
        user: InGameUser,
        data: GlobalGameData,
    ): List<ExtendedClueResponse>

    fun accept(clues: List<Clue>): Boolean
    fun accept(clue: Clue): Boolean
    fun accept(target: WithStringId): Boolean
    fun canBeAdded(container: CluesContainer): Boolean
    fun addClue(data: GlobalGameData)
    fun canBeRemovedRabdomly(container: CluesContainer): Boolean
    fun canBeRemoved(user: InGameUser, target: Any, data: GlobalGameData): Boolean
    fun anyCanBeRemoved(user: InGameUser, data: GlobalGameData): Boolean
    fun removeRandom(container: CluesContainer)
    fun removeTarget(
        target: WithStringId,
        data: GlobalGameData
    )
}