package com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import jakarta.annotation.PreDestroy
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

@Component
class GameThreadPool(
    private val gameThreadCleaner: GameThreadCleaner,
    private val gameTasksProcessor: GameTasksProcessor
) {
    lateinit var taskExecutor: ScheduledThreadPoolExecutor
    private val tasksMap: ConcurrentMap<Long, TaskCollection> = ConcurrentHashMap()
    private val loopHandlerFutures: ConcurrentMap<Long, ScheduledFuture<*>> = ConcurrentHashMap()

    private val threadId = AtomicInteger(1)

    companion object {
        val logger: Logger = LoggerFactory.getLogger(GameThreadPool::class.java)

        //TODO read from config?
        const val CORE_POOL_SIZE = 5
        const val MAX_POOL_SIZE = 1000
        const val MAX_TIME_NO_RESPONSES = 1000 * 60 * 5 // 5 min
        const val TICK_DELTA = 50L //ms
    }

    init {
        val customThreadFactory = ThreadFactory { runnable ->
            Thread(runnable, "ScheduledThread-${threadId.getAndIncrement()}")
        }
        taskExecutor = ScheduledThreadPoolExecutor(CORE_POOL_SIZE, customThreadFactory)
        taskExecutor.corePoolSize = CORE_POOL_SIZE
        taskExecutor.maximumPoolSize = MAX_POOL_SIZE
        taskExecutor.setKeepAliveTime(10, TimeUnit.SECONDS)
        taskExecutor.removeOnCancelPolicy = true
        taskExecutor.setRejectedExecutionHandler(CultpritsAbortPolicy())
    }

    fun initTickProcessingLoop(gameSession: GameSession) {
        try {
            val newTaskCollection = (TaskCollection()).apply {
                init(gameSession)
            }
            val gameId = gameSession.id!!
            tasksMap[gameId] = newTaskCollection
            logTaskExecutorInfo(gameId, "Adding tick processing loop...")
            val scheduledFuture = taskExecutor.scheduleAtFixedRate(
                {
                    gameTasksProcessor.processGameTasks(gameId, tasksMap[gameId])
                },
                0L,
                TICK_DELTA,
                TimeUnit.MILLISECONDS
            )
            loopHandlerFutures[gameId] = scheduledFuture
            logTaskExecutorInfo(gameId, "Added tick processing loop")
        } catch (th: Throwable) {
            LoggingUtils.error(
                logger,
                LoggingUtils.EVENT_ERROR,
                "Error occurred while initializing tick processing loop for game ${gameSession.id}",
                th
            )
            throw th
        }
    }

    private fun logTaskExecutorInfo(gameId: Long, reason: String) {
        LoggingUtils.withContext(
            gameId = gameId.toString(),
            eventType = LoggingUtils.EVENT_SYSTEM
        ) {
            val metrics = mapOf(
                "poolSize" to taskExecutor.poolSize,
                "activeThreads" to taskExecutor.activeCount,
                "queueSize" to taskExecutor.queue.size,
                "futuresSize" to loopHandlerFutures.size
            )

            LoggingUtils.info(
                logger,
                LoggingUtils.EVENT_SYSTEM,
                "TaskExecutor status: {} for game {}, metrics: {}",
                reason,
                gameId,
                metrics
            )
        }
    }


    @Scheduled(fixedDelay = 1000 * 60)
    fun cleanUpGames() {
        for (gameSessionId in loopHandlerFutures.keys) {
            gameThreadCleaner.cleanUpGameSession(gameSessionId, loopHandlerFutures, tasksMap)
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
            LoggingUtils.withContext(
                gameId = gameId.toString(),
                userId = task.userAccount.id.toString(),
                eventType = LoggingUtils.EVENT_SYSTEM
            ) {
                LoggingUtils.debug(
                    logger,
                    LoggingUtils.EVENT_SYSTEM,
                    "Task added to game processing queue, channelId: {}, messageType: {}",
                    task.channelId,
                    task.nettyRequestMessage.javaClass.simpleName
                )
            }
        } else {
            LoggingUtils.debug(
                logger,
                LoggingUtils.EVENT_SYSTEM,
                "Task skipped (no task collection found), gameId: {}",
                gameId
            )
        }
    }

    @PreDestroy
    fun onDestroy() {
        val handlerCount = loopHandlerFutures.size
        for (handler in loopHandlerFutures.values) {
            handler.cancel(true)
        }
        LoggingUtils.info(
            logger,
            LoggingUtils.EVENT_SYSTEM,
            "Application shutting down: {} game loop handlers cancelled",
            handlerCount
        )
        loopHandlerFutures.clear()
    }

}
