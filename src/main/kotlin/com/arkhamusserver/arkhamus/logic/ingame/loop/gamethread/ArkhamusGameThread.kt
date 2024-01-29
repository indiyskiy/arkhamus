package com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread

import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisGameRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyTickRequestMessage
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import kotlin.jvm.optionals.getOrNull

class ArkhamusGameThread(
    private val gameRepository: RedisGameRepository
) : Runnable {

    private val gamesMap: ConcurrentMap<Long, GameSession> = ConcurrentHashMap()
    private val tasksMap: ConcurrentMap<Long, TaskCollection> = ConcurrentHashMap()

    override fun run() {
        while (true) {
            gamesMap.forEach {
                processGameTasks(it)
            }
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
            addResponse(it)
        }
        flush()
        cleanUp(tick)
        updateNextTick(tick, game)
    }

    private fun updateNextTick(tick: Long, game: RedisGame) {
        game.currentTick = tick + 1
        gameRepository.save(game)
    }

    private fun addResponse(request: NettyTickRequestMessage) {
        TODO("Not yet implemented")
    }

    private fun cleanUp(tick: Long) {

    }

    private fun process(request: NettyTickRequestMessage) {
        TODO("Not yet implemented")
    }

    private fun flush() {
        TODO("Not yet implemented")
    }

    fun isThreadOfGame(gameId: Long) =
        gamesMap.contains(gameId)

}