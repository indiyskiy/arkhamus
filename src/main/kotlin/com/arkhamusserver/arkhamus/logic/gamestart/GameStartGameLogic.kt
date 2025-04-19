package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.InRamGameRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.GameSessionRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.enums.ingame.core.God
import com.arkhamusserver.arkhamus.model.ingame.InRamGame
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp
import kotlin.random.Random

@Component
class GameStartGameLogic(
    private val inRamGameRepository: InRamGameRepository,
    private val gameSessionRepository: GameSessionRepository
) {
    companion object {
        private val random: Random = Random(System.currentTimeMillis())
    }

    @Transactional
    fun createTheGame(game: GameSession) {
        game.god = God.values().random(random)
        game.state = GameState.PENDING
        game.startedTimestamp = Timestamp(System.currentTimeMillis())
        gameSessionRepository.save(game)
    }

    @Transactional
    fun createInRamGame(game: GameSession) {
        inRamGameRepository.save(
            InRamGame(
                id = game.id.toString(),
                gameId = game.id!!,
                god = game.god!!
            )
        )
    }

}