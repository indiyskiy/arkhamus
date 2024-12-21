package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.EACH_CLUE_ON_START
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.clues.ClueHandler
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.ZoneType
import com.arkhamusserver.arkhamus.model.redis.RedisLevelZone
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class GameStartClueLogic(
    private val clueHandler: ClueHandler
) {

    @Transactional
    fun createClues(
        game: GameSession,
        zones: List<RedisLevelZone>
    ) {
        val clueZones = zones.filter { it.zoneType == ZoneType.CLUE }
        game.god?.let {
            it.getTypes().forEach { clue ->
                clueHandler.addClues(game, clueZones, clue, EACH_CLUE_ON_START)
            }
        }
    }

}