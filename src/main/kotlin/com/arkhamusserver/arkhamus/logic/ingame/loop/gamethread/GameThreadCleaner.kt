package com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread

import com.arkhamusserver.arkhamus.config.netty.ChannelRepository
import com.arkhamusserver.arkhamus.config.repository.InGameRepositoryCleaner
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.InGameDataAccess
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.ingame.InRamGame
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.ScheduledFuture

@Service
class GameThreadCleaner(
    private val inGameDataAccess: InGameDataAccess,
    private val channelRepository: ChannelRepository,
    private val inGameRepositoryCleaner: InGameRepositoryCleaner,
) {

    companion object {
        private val logger = LoggingUtils.getLogger<GameThreadCleaner>()
    }

    @Transactional
    fun cleanUpGameSession(
        gameSessionId: Long,
        loopHandlerFutures: ConcurrentMap<Long, ScheduledFuture<*>>,
        tasksMap: ConcurrentMap<Long, TaskCollection>
    ) {
        val inRamGameSession = inGameDataAccess.getGame(gameSessionId)
        closeFinished(inRamGameSession, gameSessionId, loopHandlerFutures, tasksMap)
    }

    private fun closeFinished(
        inRamGameSession: InRamGame?,
        gameSessionId: Long,
        loopHandlerFutures: ConcurrentMap<Long, ScheduledFuture<*>>,
        tasksMap: ConcurrentMap<Long, TaskCollection>
    ) {
        if (inRamGameSession?.state == GameState.FINISHED.name) {
            logger.info("Trying to end $gameSessionId")
            cleanGameLoopFuture(gameSessionId, loopHandlerFutures, tasksMap)
            logger.info("close all connections")
            inRamGameSession.gameId.let {
                val channels = channelRepository.getByGameId(it)
                channels.forEach {
                    channelRepository.closeAndRemove(it)
                }
            }
            logger.info("cleaning in-ram database")
            inRamGameSession.gameId.let {
                inGameRepositoryCleaner.cleanGame(it)
            }
        }
    }

    private fun cleanGameLoopFuture(
        gameSessionId: Long,
        loopHandlerFutures: ConcurrentMap<Long, ScheduledFuture<*>>,
        tasksMap: ConcurrentMap<Long, TaskCollection>,
    ) {
        loopHandlerFutures[gameSessionId]?.let {
            if (!it.isCancelled) {
                val canceled = it.cancel(false)
                if (canceled) {
                    loopHandlerFutures.remove(gameSessionId)
                    tasksMap.remove(gameSessionId)
                    logger.info("Not canceled: Loop handler stopped for game session $gameSessionId with cancel")
                    logger.info("Not canceled: loop handler futures size: ${loopHandlerFutures.size}")
                }
            } else {
                loopHandlerFutures.remove(gameSessionId)
                tasksMap.remove(gameSessionId)
                logger.info("Canceled: Loop handler stopped for game session $gameSessionId without cancel")
                logger.info("Canceled: loop handler futures size: ${loopHandlerFutures.size}")
            }
        }
    }
}