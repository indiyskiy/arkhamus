package com.arkhamusserver.arkhamus.logic

import com.arkhamusserver.arkhamus.view.dto.GameSessionDto
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Component

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