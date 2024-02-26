package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class GameStartLogic(
    private val gameStartContainerLogic: GameStartContainerLogic,
    private val gameStartUserLogic: GameStartUserLogic,
    private val gameStartGameLogic: GameStartGameLogic,
    private val gameStartTimeEventLogic: GameStartTimeEventLogic
) {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(GameStartLogic::class.java)
        val random: Random = Random(System.currentTimeMillis())
    }

    fun startGame(game: GameSession) {
        game.gameSessionSettings.level?.levelId?.let { levelId ->
            gameStartGameLogic.createTheGame(game)
            gameStartUserLogic.createGameUsers(levelId, game)
            gameStartContainerLogic.createContainers(levelId, game)
            gameStartTimeEventLogic.createStartEvents(game)
        }
    }

}