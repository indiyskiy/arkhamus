package com.arkhamusserver.arkhamus.logic

import com.arkhamusserver.arkhamus.model.database.entity.GameType
import com.arkhamusserver.arkhamus.view.dto.GameSessionDto
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component

@Component
class DefaultGameLogic(
    private val gameLogic: GameLogic,
    private val singleGameLogic: SingleGameLogic,
    private val customGameLogic: CustomGameLogic,
) {
    @Transactional
    fun start(gameId: Long): GameSessionDto? {
        val game = gameLogic.findGameNullSafe(gameId)
        return when (game.gameType) {
            GameType.DEFAULT -> null
            GameType.CUSTOM -> customGameLogic.start(game)
            GameType.SINGLE -> singleGameLogic.start(game)
        }
    }
}