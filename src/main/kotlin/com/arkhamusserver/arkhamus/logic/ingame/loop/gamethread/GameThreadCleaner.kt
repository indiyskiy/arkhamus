package com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread

import com.arkhamusserver.arkhamus.config.netty.ChannelRepository
import com.arkhamusserver.arkhamus.config.redis.RedisCleaner
import com.arkhamusserver.arkhamus.logic.ingame.GameEndLogic
import com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread.GameThreadPool.Companion.MAX_TIME_NO_RESPONSES
import com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread.GameThreadPool.Companion.logger
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.RedisDataAccess
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisGameUserRepository
import com.arkhamusserver.arkhamus.model.enums.GameEndReason
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.ScheduledFuture
import kotlin.collections.forEach

@Service
class GameThreadCleaner(
    private val redisDataAccess: RedisDataAccess,
    private val redisGameUserRepository: RedisGameUserRepository,
    private val gameEndLogic: GameEndLogic,
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
        redisGameSession?.let {
            val users = redisGameUserRepository.findByGameId(gameSessionId)
            logger.info("all users of the game $gameSessionId are: ${users.joinToString { it.userId.toString() }}")
            if (users.isEmpty()) {
                val allUsers = redisGameUserRepository.findAll()
                logger.warn("list is empty. all users are: ${allUsers.joinToString { "${it.userId} - ${it.gameId}" }}")
            }
            markLeaversIfNoResponses(it, users)
            abandonIfAllLeave(it, users)
        }
        closeFinished(redisGameSession, gameSessionId, loopHandlerFutures, tasksMap)
    }


    private fun abandonIfAllLeave(game: RedisGame, users: List<RedisGameUser>) {
        if (users.all { it.leftTheGame }) {
            logger.info("all users ${users.joinToString { it.userId.toString() }} leave from the game ${game.id}, abandoning..")
            gameEndLogic.endTheGame(game, users.associateBy { it.userId }, GameEndReason.ABANDONED, 10_000)
        }
    }

    private fun markLeaversIfNoResponses(game: RedisGame, users: List<RedisGameUser>) {
        if (game.globalTimer - game.lastTimeSentResponse > MAX_TIME_NO_RESPONSES) {
            logger.info("no responses too long (${game.globalTimer} now, last was ${game.lastTimeSentResponse}) for game ${game.id}, marking all users as leavers")
            users.forEach { it.leftTheGame = true }
            redisGameUserRepository.saveAll(users)
        }
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
}