package com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread

import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisGameRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyTickRequestMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentMap
import kotlin.jvm.optionals.getOrNull

class ArkhamusGameThreadLoopLogic(
    private val gameRepository: RedisGameRepository,
    private val gamesMap: ConcurrentMap<Long, GameSession>,
    private val tasksMap: ConcurrentMap<Long, TaskCollection>
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
        gamesMap[gameSession.id] = gameSession
        locker.notify()
        logger.info("endless game loop has awakened")
    }

    fun addTask(gameSession: GameSession, requestMessage: NettyTickRequestMessage) {
        val taskCollection = tasksMap[gameSession.id]
        if (taskCollection == null) {
            tasksMap[gameSession.id] = TaskCollection().apply {
                this.taskList.add(requestMessage)
            }
        } else {
            taskCollection.taskList.add(requestMessage)
        }
        locker.notify()
        logger.info("endless game loop has awakened")
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
                val usersOfCurrentTasks = currentTasks.map { it.userId() }.toSet()

                if (usersOfCurrentTasks == usersOfGame) {
                    processCurrentTasks(currentTasks, tick, ongoingGame)
                }
            }
        }
    }

    private fun processCurrentTasks(
        currentTasks: List<NettyTickRequestMessage>,
        tick: Long,
        game: RedisGame
    ) {
        currentTasks.forEach {
            process(it)
        }
        currentTasks.forEach {
            addResponse(it, game)
        }
        flush()
        cleanUp(tick, game)
        updateNextTick(tick, game)
    }

    private fun updateNextTick(tick: Long, game: RedisGame) {
        game.currentTick = tick + 1
        gameRepository.save(game)
    }

    private fun addResponse(request: NettyTickRequestMessage, game: RedisGame) {
        TODO("Not yet implemented")
    }

    private fun cleanUp(tick: Long, game: RedisGame) {

    }

    private fun process(request: NettyTickRequestMessage) {
        TODO("Not yet implemented")
    }

    private fun flush() {
        TODO("Not yet implemented")
    }
}