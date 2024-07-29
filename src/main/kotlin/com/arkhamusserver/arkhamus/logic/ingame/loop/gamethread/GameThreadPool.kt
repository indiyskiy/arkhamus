package com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread

import com.arkhamusserver.arkhamus.config.netty.ChannelRepository
import com.arkhamusserver.arkhamus.config.redis.RedisCleaner
import com.arkhamusserver.arkhamus.logic.ingame.loop.ArkhamusOneTickLogic
import com.arkhamusserver.arkhamus.logic.ingame.loop.ArkhamusOneTickLogic.Companion.TICK_DELTA
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.RedisDataAccess
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.ResponseSendingLoopManager
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.*

@Component
class GameThreadPool(
    private val redisDataAccess: RedisDataAccess,
    private val responseSendingLoopManager: ResponseSendingLoopManager,
    private val tickLogic: ArkhamusOneTickLogic,
    private val redisCleaner: RedisCleaner,
    private val channelRepository: ChannelRepository
) {
    private val taskExecutor: ScheduledThreadPoolExecutor = ScheduledThreadPoolExecutor(CORE_POOL_SIZE)
    private val tasksMap: ConcurrentMap<Long, TaskCollection> = ConcurrentHashMap()
    private val loopHandlerFutures: ConcurrentMap<Long, ScheduledFuture<*>> = ConcurrentHashMap()

    companion object {
        val logger: Logger = LoggerFactory.getLogger(GameThreadPool::class.java)

        //TODO read from config?
        const val CORE_POOL_SIZE = 3
    }

    init {
        taskExecutor.corePoolSize = CORE_POOL_SIZE
    }

    fun initTickProcessingLoop(gameSession: GameSession) {
        try {
            val newTaskCollection = (TaskCollection()).apply {
                init(gameSession)
            }
            val gameId = gameSession.id!!
            tasksMap[gameId] = newTaskCollection
            val scheduledFuture = taskExecutor.scheduleAtFixedRate(
                {
                    processGameTasks(gameId)
                },
                0L,
                TICK_DELTA,
                TimeUnit.MILLISECONDS
            )
            loopHandlerFutures[gameSession.id] = scheduledFuture
        } catch (th: Throwable) {
            logger.error("Error occurred while initializing tick processing loop", th)
            throw th
        }

    }

    private fun processGameTasks(gameId: Long) {
        try {
            val taskCollection = tasksMap[gameId]!!
            val taskList = synchronized(taskCollection) {
                taskCollection.getList().also { taskCollection.resetList() }
            }
            val redisGame = redisDataAccess.getGame(gameId)
            // TODO better state handling - e.g. we might want to support pause somehow and other stuff later
            if (redisGame != null && redisGame.state in GameState.gameInProgressStateStrings) {
                processGameTick(
                    tasks = taskList,
                    gameId = gameId,
                    ongoingGame = redisGame
                )
            }
        } catch (e: Throwable) {
            logger.error("error on processing game tasks for game $gameId", e)
            throw e
        }

    }

    @Scheduled(fixedRate = 10_000)
    fun cleanUpGames() {
        for (gameSessionId in loopHandlerFutures.keys) {
            val redisGameSession = redisDataAccess.getGame(gameSessionId)
            if (redisGameSession?.state == GameState.FINISHED.name) {
                logger.info("Trying to end $gameSessionId")
                cleanGameLoopFuture(gameSessionId)
                logger.info("close all connections")
                redisGameSession.gameId?.let {
                    val channels = channelRepository.getByGameId(it)
                    channels.forEach {
                        channelRepository.closeAndRemove(it)
                    }
                }
                logger.info("cleaning redis database")
                redisGameSession.gameId?.let { redisCleaner.cleanGame(it) } ?: redisCleaner.cleanGameWithoutGameId(
                    redisGameSession
                )
            }
        }
    }

    private fun cleanGameLoopFuture(gameSessionId: Long) {
        if (loopHandlerFutures[gameSessionId]?.isCancelled == true) {
            loopHandlerFutures.remove(gameSessionId)
            tasksMap.remove(gameSessionId)
            logger.info("Loop handler stopped for game session $gameSessionId")
            return
        }
        loopHandlerFutures[gameSessionId]?.cancel(false)
    }

    fun addTask(task: NettyTickRequestMessageDataHolder) {
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
        tasks: List<NettyTickRequestMessageDataHolder>,
        gameId: Long,
        ongoingGame: RedisGame
    ) {
        try {
            val responses = tickLogic.processCurrentTasks(
                tasks,
                ongoingGame
            )
            responseSendingLoopManager.addResponses(responses, gameId)
        } catch (e: Throwable) {
            logger.error("error on processing game tick for game $gameId", e)
            throw e
        }

    }

}