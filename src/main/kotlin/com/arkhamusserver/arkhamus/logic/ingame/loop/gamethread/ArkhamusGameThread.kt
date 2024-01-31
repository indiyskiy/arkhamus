package com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread

import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisGameRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

class ArkhamusGameThread(private val gameRepository: RedisGameRepository) : Runnable {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(ArkhamusGameThread::class.java)
    }

    private val gamesMap: ConcurrentMap<Long, GameSession> = ConcurrentHashMap()
    private val tasksMap: ConcurrentMap<Long, TaskCollection> = ConcurrentHashMap()
    private var threadLogic: ArkhamusGameThreadLoopLogic? = null

    override fun run() {
        logger.info("Thread started")
        try {
            threadLogic = ArkhamusGameThreadLoopLogic(gameRepository, gamesMap, tasksMap)
            threadLogic?.run()
        } finally {
            logger.info("Thread dead")
        }
    }

    fun isThreadOfGame(gameId: Long) =
        gamesMap.contains(gameId)

    fun size(): Int {
        return gamesMap.size
    }

    fun addGame(gameSession: GameSession) {
        gamesMap[gameSession.id] = gameSession
    }

}