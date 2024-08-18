package com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread

import com.arkhamusserver.arkhamus.logic.ingame.loop.ArkhamusOneTickLogic.Companion.TICK_DELTA
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import jakarta.annotation.PreDestroy
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.*

@Component
class GameThreadPool(
    private val gameThreadCleaner: GameThreadCleaner,
    private val gameTasksProcessor: GameTasksProcessor
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
                    gameTasksProcessor.processGameTasks(gameId, tasksMap[gameId])
                },
                0L,
                TICK_DELTA,
                TimeUnit.MILLISECONDS
            )
            loopHandlerFutures[gameId] = scheduledFuture
            logger.info("Added tick processing loop for game session $gameId")
            logger.info("loop handler futures size: ${loopHandlerFutures.size}")
        } catch (th: Throwable) {
            logger.error("Error occurred while initializing tick processing loop", th)
            throw th
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
            logger.debug("task added")
        } else {
            logger.debug("task skipped")
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