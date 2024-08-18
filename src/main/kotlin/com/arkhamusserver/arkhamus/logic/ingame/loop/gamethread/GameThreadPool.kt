package com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread

import com.arkhamusserver.arkhamus.config.netty.ChannelRepository
import com.arkhamusserver.arkhamus.config.redis.RedisCleaner
import com.arkhamusserver.arkhamus.logic.ingame.GameEndLogic
import com.arkhamusserver.arkhamus.logic.ingame.loop.ArkhamusOneTickLogic
import com.arkhamusserver.arkhamus.logic.ingame.loop.ArkhamusOneTickLogic.Companion.TICK_DELTA
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.RedisDataAccess
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.ResponseSendingLoopManager
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisGameUserRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.enums.GameEndReason
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import jakarta.annotation.PreDestroy
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
    private val channelRepository: ChannelRepository,
    private val redisGameUserRepository: RedisGameUserRepository,
    private val gameEndLogic: GameEndLogic
) {
    private val taskExecutor: ScheduledThreadPoolExecutor = ScheduledThreadPoolExecutor(CORE_POOL_SIZE)
    private val tasksMap: ConcurrentMap<Long, TaskCollection> = ConcurrentHashMap()
    private val loopHandlerFutures: ConcurrentMap<Long, ScheduledFuture<*>> = ConcurrentHashMap()

    companion object {
        val logger: Logger = LoggerFactory.getLogger(GameThreadPool::class.java)

        //TODO read from config?
        const val CORE_POOL_SIZE = 3
        const val MAX_TIME_NO_RESPONSES = 1000 * 60 * 5 // 5 min
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
            logger.info("Adding tick processing loop for game session $gameId...")
            val scheduledFuture = taskExecutor.scheduleAtFixedRate(
                {
                    processGameTasks(gameId)
                },
                0L,
                TICK_DELTA,
                TimeUnit.MILLISECONDS
            )
            loopHandlerFutures[gameSession.id] = scheduledFuture
            logger.info("Added tick processing loop for game session $gameId")
            logger.info("loop handler futures size: ${loopHandlerFutures.size}")
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
//            throw e
        }

    }

    @Scheduled(fixedDelay = 10_000)
    fun cleanUpGames() {
        for (gameSessionId in loopHandlerFutures.keys) {
            val redisGameSession = redisDataAccess.getGame(gameSessionId)
            redisGameSession?.let {
                val users = redisGameUserRepository.findByGameId(gameSessionId)
                markLeaversIfNoResponses(it, users)
                abandonIfAllLeave(it, users)
            }
            closeFinished(redisGameSession, gameSessionId)
        }
    }

    private fun abandonIfAllLeave(game: RedisGame, users: List<RedisGameUser>) {
        if (users.all { it.livedTheGame }) {
            logger.info("all users ${users.joinToString(){it.userId.toString()}} leave from the game ${game.id}, abandoning..")
            gameEndLogic.endTheGame(game, users.associateBy { it.userId }, GameEndReason.ABANDONED)
            gameEndLogic.endTheGameCompletely(game)
        }
    }

    private fun markLeaversIfNoResponses(game: RedisGame, users: List<RedisGameUser>) {
        if (game.globalTimer - game.lastTimeSentResponse > MAX_TIME_NO_RESPONSES) {
            logger.info("no responses too long (${game.globalTimer} now, last was ${game.lastTimeSentResponse}) for game ${game.id}, marking all users as leavers")
            users.forEach { it.livedTheGame = true }
            redisGameUserRepository.saveAll(users)
        }
    }

    private fun closeFinished(
        redisGameSession: RedisGame?,
        gameSessionId: Long
    ) {
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
            redisGameSession.gameId?.let {
                redisCleaner.cleanGame(it)
            } ?: redisCleaner.cleanGameWithoutGameId(
                redisGameSession
            )
        }
    }

    private fun cleanGameLoopFuture(gameSessionId: Long) {
        loopHandlerFutures[gameSessionId]?.let {
            if (!it.isCancelled) {
                val canceled = it.cancel(false)
                if (canceled) {
                    loopHandlerFutures.remove(gameSessionId)
                    tasksMap.remove(gameSessionId)
                    logger.info("Loop handler stopped for game session $gameSessionId with cancel")
                    logger.info("loop handler futures size: ${loopHandlerFutures.size}")
                }
            } else {
                loopHandlerFutures.remove(gameSessionId)
                tasksMap.remove(gameSessionId)
                logger.info("Loop handler stopped for game session $gameSessionId without cancel")
                logger.info("loop handler futures size: ${loopHandlerFutures.size}")
            }
        }

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

    @PreDestroy
    fun onDestroy() {
        for (handler in loopHandlerFutures.values) {
            handler.cancel(true)
            logger.info("Loop handler cancelled")
        }
        loopHandlerFutures.clear()
    }

}