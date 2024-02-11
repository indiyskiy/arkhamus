package com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread

import com.arkhamusserver.arkhamus.logic.ingame.loop.ArkhamusOneTickLogic
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.RedisDataAccess
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.ResponseSendingLoopManager
import com.arkhamusserver.arkhamus.model.dataaccess.redis.GameRedisRepository
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import kotlin.jvm.optionals.getOrNull

@Component
class GameThreadPool(
    private val redisDataAccess: RedisDataAccess,
    private val responseSendingLoopManager: ResponseSendingLoopManager,
    private val tickLogic: ArkhamusOneTickLogic
) {
    private val taskExecutor: ThreadPoolTaskExecutor = ThreadPoolTaskExecutor()
    private val tasksMap: ConcurrentMap<Long, TaskCollection> = ConcurrentHashMap()

    companion object {
        val logger: Logger = LoggerFactory.getLogger(GameThreadPool::class.java)
    }

    init {
        taskExecutor.corePoolSize = 3
        taskExecutor.maxPoolSize = 5
        taskExecutor.initialize()
    }


    fun addTask(task: NettyTickRequestMessageContainer) {
        val gameId = task.gameSession!!.id!!
        val taskCollection = tasksMap[gameId]
        if (taskCollection != null) {
            tasksMap[gameId]?.add(task)
            processIfEnoughData(gameId, taskCollection)
        } else {
            val createdTaskCollection = (TaskCollection()).apply {
                init(task.gameSession!!)
                add(task)
            }
            tasksMap[gameId] = createdTaskCollection
            processIfEnoughData(gameId, createdTaskCollection)
        }
    }

    private fun processIfEnoughData(
        gameId: Long,
        taskCollection: TaskCollection
    ) {
        if (!taskCollection.isEmpty()) {
            val ongoingGame = redisDataAccess.getGame(gameId)
            processGameTickIfReady(ongoingGame, taskCollection, gameId)
        }
    }

    private fun processGameTickIfReady(
        ongoingGame: RedisGame,
        taskCollection: TaskCollection,
        gameId: Long
    ) {
        val tick = ongoingGame.currentTick
        val usersOfGame = taskCollection.userIds()
        val currentTasks = taskCollection.getByTick(tick)
        val usersOfCurrentTasks = currentTasks.mapNotNull { it.userAccount.id }.toSet()
        if (usersOfCurrentTasks == usersOfGame) {
            taskExecutor.execute {
                processGameTick(taskCollection.getList(), gameId, tick, ongoingGame)
            }
        }
    }

    private fun processGameTick(
        tasks: MutableList<NettyTickRequestMessageContainer>,
        gameId: Long,
        tick: Long,
        ongoingGame: RedisGame
    ) {
        val responses = tickLogic.processCurrentTasks(
            tasks,
            tick,
            ongoingGame
        )
        responseSendingLoopManager.addResponses(responses, gameId)
    }

}