package com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread

import com.arkhamusserver.arkhamus.logic.ingame.loop.ArkhamusOneTickLogic
import com.arkhamusserver.arkhamus.logic.ingame.loop.ArkhamusOneTickLogic.Companion.TICK_DELTA
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.RedisDataAccess
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.ResponseSendingLoopManager
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

@Component
class GameThreadPool(
    private val redisDataAccess: RedisDataAccess,
    private val responseSendingLoopManager: ResponseSendingLoopManager,
    private val tickLogic: ArkhamusOneTickLogic
) {
    private val taskExecutor: ScheduledThreadPoolExecutor = ScheduledThreadPoolExecutor(CORE_POOL_SIZE)
    private val tasksMap: ConcurrentMap<Long, TaskCollection> = ConcurrentHashMap()

    companion object {
        val logger: Logger = LoggerFactory.getLogger(GameThreadPool::class.java)

        //TODO read from config?
        const val CORE_POOL_SIZE = 3
    }

    init {
        taskExecutor.corePoolSize = CORE_POOL_SIZE
    }

    fun initTickProcessingLoop(gameSession: GameSession) {
        val taskCollection = (TaskCollection()).apply {
            init(gameSession)
        }
        val gameId = gameSession.id!!
        tasksMap[gameId] = taskCollection
        taskExecutor.scheduleAtFixedRate({
            val taskCollection = tasksMap[gameId]!!
            val taskList = synchronized(taskCollection) {
                taskCollection.getList().also { taskCollection.resetList() }
            }
            processGameTick(
                tasks = taskList,
                gameId = gameId,
                ongoingGame = redisDataAccess.getGame(gameId)
            )
        },
            0L,
            TICK_DELTA,
            TimeUnit.MILLISECONDS)
    }

    fun addTask(task: NettyTickRequestMessageContainer) {
        val gameId = task.gameSession!!.id!!
        val taskCollection = tasksMap[gameId]
        val added: Boolean = if (taskCollection != null) {
            synchronized(taskCollection) {
                taskCollection.add(task)
            }
        } else {
            false
        }
        if (added) {
            logger.debug("task added")
        } else {
            logger.debug("task skipped")
        }
    }

    private fun processGameTick(
        tasks: List<NettyTickRequestMessageContainer>,
        gameId: Long,
        ongoingGame: RedisGame
    ) {
        val responses = tickLogic.processCurrentTasks(
            tasks,
            ongoingGame
        )
        responseSendingLoopManager.addResponses(responses, gameId)
    }

}