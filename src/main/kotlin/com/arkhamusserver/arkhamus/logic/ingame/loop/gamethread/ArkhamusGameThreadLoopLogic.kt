package com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.NettyTickRequestMessageContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.ResponseSendingLoopManager
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisGameRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentMap
import kotlin.jvm.optionals.getOrNull

class ArkhamusGameThreadLoopLogic(
    private val gameRepository: RedisGameRepository,
    private val gamesMap: ConcurrentMap<Long, GameSession>,
    private val tasksMap: ConcurrentMap<Long, TaskCollection>,
    private val gameResponseBuilder: GameResponseBuilder,
    private val responseSendingLoopManager: ResponseSendingLoopManager
) : Runnable {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(ArkhamusGameThreadLoopLogic::class.java)
    }

    private val locker = Object()
    override fun run() {
        synchronized(locker) {
            logger.info("endless game loop started")
            while (true) {
                if (gamesMap.isEmpty() || tasksMap.isEmpty()) {
                    try {
                        logger.info("endless game loop goes to sleep")
                        locker.wait()
                    } catch (e: InterruptedException) {
                        Thread.currentThread().interrupt()
                        throw RuntimeException(e)
                    }
                }
                gamesMap.forEach {
                    processGameTasks(it)
                }
            }
        }
    }

    fun addGame(gameSession: GameSession) {
        if (!gamesMap.contains(gameSession.id)) {
            gamesMap[gameSession.id] = gameSession
            locker.notify()
            logger.info("endless game loop has awakened")
        }
    }

    fun addTask(container: NettyTickRequestMessageContainer) {
        container.arkhamusChannel.gameSession?.let {
            val taskCollection = tasksMap[it.id]
            if (taskCollection == null) {
                tasksMap[it.id] = TaskCollection().apply {
                    this.taskList.add(container)
                }
            } else {
                taskCollection.taskList.add(container)
            }
            locker.notify()
            logger.info("endless game loop has awakened")
        }
    }

    private fun processGameTasks(gameIdToGameSession: Map.Entry<Long, GameSession>) {
        val gameId = gameIdToGameSession.key
        val taskCollection = tasksMap[gameId]
        if (taskCollection != null && !taskCollection.isEmpty()) {
            val gameSession = gameIdToGameSession.value
            val ongoingGame = gameRepository.findById(gameId.toString()).getOrNull()
            if (ongoingGame != null) {
                val tick = ongoingGame.currentTick

                val usersOfGame =
                    gameSession.usersOfGameSession.map { it.userAccount.id }.toSet()
                val currentTasks = taskCollection.getByTick(tick)
                val usersOfCurrentTasks = currentTasks.mapNotNull { it.arkhamusChannel.userAccount?.id }.toSet()

                if (usersOfCurrentTasks == usersOfGame) {
                    processCurrentTasks(gameId, currentTasks, tick, ongoingGame)
                }
            }
        }
    }

    private fun processCurrentTasks(
        gameId: Long,
        currentTasks: List<NettyTickRequestMessageContainer>,
        tick: Long,
        game: RedisGame
    ) {
        val newTime = currentTasks.maxBy { it.registrationTime }.registrationTime
        currentTasks.forEach {
            process(it, tick, game)
        }
        currentTasks.forEach {
            addResponse(it, tick, game)
        }
        flush(tick, game, gameId)
        cleanUp(tick, gameId)
        updateNextTick(tick, newTime, game)
    }

    private fun updateNextTick(tick: Long, newTime: Long, game: RedisGame) {
        game.currentTick = tick + 1
        game.globalTimer = newTime
        gameRepository.save(game)
    }

    private fun addResponse(
        request: NettyTickRequestMessageContainer,
        tick: Long,
        game: RedisGame
    ) {
        val response = gameResponseBuilder.buildResponse(request, tick, game)
        responseSendingLoopManager.addResponse(response, tick, game)
    }

    private fun cleanUp(tick: Long, gameId: Long) {
        tasksMap[gameId]?.filterOut(tick)
    }

    private fun process(
        request: NettyTickRequestMessageContainer,
        tick: Long,
        game: RedisGame
    ) {
        TODO("Not yet implemented")
    }

    private fun flush(tick: Long, game: RedisGame, gameId: Long) {
        responseSendingLoopManager.flush(tick, game, gameId)
    }
}


