package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.EACH_CLUE_ON_START
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.clues.AdvancedClueHandler
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.ingame.InGameLevelZone
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class GameStartClueLogic(
    private val clueHandlers: List<AdvancedClueHandler>,
) {

    @Transactional
    fun createClues(
        game: GameSession,
        zones: List<InGameLevelZone>
    ) {
        clueHandlers.forEach { handler ->
            handler.addClues(
                game,
                game.god!!,
                zones,
                EACH_CLUE_ON_START
            )
        }
    }

}