package com.arkhamusserver.arkhamus.logic

import com.arkhamusserver.arkhamus.view.dto.GameSessionDto
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class DefaultGameLogic(
    private val gameLogic: GameLogic,
) {
    @Transactional
    fun start(gameId: Long): GameSessionDto {
        val game = gameLogic.findGameNullSafe(gameId)
        return gameLogic.start(game)
    }
}