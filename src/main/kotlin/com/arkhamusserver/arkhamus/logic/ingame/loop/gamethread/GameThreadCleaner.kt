package com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread

import com.arkhamusserver.arkhamus.config.netty.ChannelRepository
import com.arkhamusserver.arkhamus.config.redis.RedisCleaner
import com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread.GameThreadPool.Companion.logger
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.RedisDataAccess
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.ScheduledFuture

@Service
class GameThreadCleaner(
    private val redisDataAccess: RedisDataAccess,
    private val channelRepository: ChannelRepository,
    private val redisCleaner: RedisCleaner,
) {

    @Transactional
    fun cleanUpGameSession(
        gameSessionId: Long,
        loopHandlerFutures: ConcurrentMap<Long, ScheduledFuture<*>>,
        tasksMap: ConcurrentMap<Long, TaskCollection>
    ) {
        val redisGameSession = redisDataAccess.getGame(gameSessionId)
        closeFinished(redisGameSession, gameSessionId, loopHandlerFutures, tasksMap)
    }

    private fun closeFinished(
        redisGameSession: RedisGame?,
        gameSessionId: Long,
        loopHandlerFutures: ConcurrentMap<Long, ScheduledFuture<*>>,
        tasksMap: ConcurrentMap<Long, TaskCollection>
    ) {
        if (redisGameSession?.state == GameState.FINISHED.name) {
            logger.info("Trying to end $gameSessionId")
            cleanGameLoopFuture(gameSessionId, loopHandlerFutures, tasksMap)
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