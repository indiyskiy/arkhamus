package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.EACH_CLUE_ON_START
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.clues.AdvancedClueHandler
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.redis.RedisLevelZone
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class GameStartClueLogic(
    private val clueHandlers: List<AdvancedClueHandler>,
) {

    @Transactional
    fun createClues(
        game: GameSession,
        zones: List<RedisLevelZone>
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