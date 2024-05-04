package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread.GameThreadPool
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class GameStartLogic(
    private val gameStartContainerLogic: GameStartContainerLogic,
    private val gameStartCrafterLogic: GameStartCrafterLogic,
    private val gameStartLanternLogic: GameStartLanternLogic,
    private val gameStartUserLogic: GameStartUserLogic,
    private val gameStartGameLogic: GameStartGameLogic,
    private val gameStartTimeEventLogic: GameStartTimeEventLogic,
    private val gameThreadPool: GameThreadPool
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
            gameStartCrafterLogic.createCrafters(levelId, game)
            gameStartLanternLogic.createLanterns(levelId, game)
            gameStartTimeEventLogic.createStartEvents(game)
        }
        gameThreadPool.initTickProcessingLoop(game)
    }

}