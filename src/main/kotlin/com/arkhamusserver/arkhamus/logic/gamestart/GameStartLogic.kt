package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread.GameThreadPool
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.random.Random

@Component
class GameStartLogic(
    private val gameStartContainerLogic: GameStartContainerLogic,
    private val gameStartCrafterLogic: GameStartCrafterLogic,
    private val gameStartLanternLogic: GameStartLanternLogic,
    private val gameStartAltarLogic: GameStartAltarLogic,
    private val gameStartUserLogic: GameStartUserLogic,
    private val gameStartGameLogic: GameStartGameLogic,
    private val gameStartTimeEventLogic: GameStartTimeEventLogic,
    private val gameStartLevelZoneLogic: GameStartLevelZoneLogic,
    private val gameStartClueLogic: GameStartClueLogic,
    private val gameStartQuestLogic: GameStartQuestLogic,
    private val gameThreadPool: GameThreadPool,
    private val gameStartVoteSpotLogic: GameStartVoteSpotLogic,
    private val gameStartThresholdLogic: GameStartThresholdLogic,
    private val gameStartDoorLogic: GameStartDoorLogic,
) {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(GameStartLogic::class.java)
        val random: Random = Random(System.currentTimeMillis())
    }

    @Transactional
    fun startGame(game: GameSession) {
        game.gameSessionSettings.level?.levelId?.let { levelId ->
            gameStartGameLogic.createTheGame(game)
            gameStartGameLogic.createTheRedisGame(game)
            gameStartUserLogic.leaveFromPreviousGames(game)
            val users = gameStartUserLogic.createGameUsers(levelId, game)
            gameStartContainerLogic.createContainers(levelId, game)
            gameStartCrafterLogic.createCrafters(levelId, game)
            gameStartLanternLogic.createLanterns(levelId, game)
            gameStartAltarLogic.createAltars(levelId, game)
            val zones = gameStartLevelZoneLogic.createLevelZones(levelId, game)
            gameStartClueLogic.createClues(game, zones)
            gameStartTimeEventLogic.createStartEvents(game)
            gameStartQuestLogic.createQuests(levelId, game, users)
            gameStartVoteSpotLogic.createVoteSpots(levelId, game, users)
            gameStartThresholdLogic.createThresholds(levelId, game)
            gameStartDoorLogic.createDoors(levelId, game)
        }
        gameThreadPool.initTickProcessingLoop(game)
    }

}